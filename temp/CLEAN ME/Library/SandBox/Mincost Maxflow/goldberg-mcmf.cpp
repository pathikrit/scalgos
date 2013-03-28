/*  [goldberg-mcmf.cpp] Min-cost maxflow using Goldberg's 1992 scaling push-relabel algorithm
 *    (see http://citeseer.ist.psu.edu/goldberg92efficient.html and DIMACS vol. 12, p. 157-198,
        "On Implementing Scaling Push-Relabel Algorithms for the Minimum-Cost Flow Problem")
 *    Note: the global price update heuristics (incl. refinement) are too complex to include here.
 *  v. 0.07, 2008-02-20
 *  (c) 2008 Andrey Petrov (apetrov87@gmail.com, www.cs.utexas.edu/~apetrov)
 *  Change log, with "test-mcmf" (V=300, E=10000) result in ms (for Athlon FX62):
 *    0.01:  452 (no heuristics, all data in vectors)
 *    0.02:  405 (most data in 1D and 2D malloc'd arrays [bad idea?] [not really but it made the UVA solution slower]; minor optimizations/cleanup)
 *    0.03:  481 (reverted to STL storage [weird])
 *    0.01a: 480 (from .01, inlined BFS like in .02 [small mistake there?])
 *    0.01b: 447 (eliminated path reversing in BFS)
 *    0.01c: 452 (moved cost scaling stuff under FF maxflow => more cache hits in FF [only for small N])
 *    0.01d: 513 (moved index vars u,v up to function scope [must be very confusing to the optimizer] - this is the version most like .03)
 *    0.01e: 452 (minimized scope of all vars, declaring at first assignment)
 *    0.01f: 392 (avoided the use of <cap>, as in .03)
 *    0.01g: 379 (fixed arc saturation to check only forward residual)
 *    0.04:  434 (from .01g, all data in 1D arrays, matrix row length = N rounded up to 2^k [enlarges already critical working set])
 *  Note: all changes up to this point were based on results from problem UVA10746. Then everything was re-tested.
 *    0.04a: 360 (.04 with exact row length)
 *    0.05:  215 (added push lookahead heuristic)
 *    0.05a: 174 (changed alpha from 2 to 5 [suggested in DIMACS12])
 *    0.06:  146 (added simple arc fixing)
 *    0.07:   66 (used temporary adj. list in refine; moved arc fixing into saturation loop)
 */

//NOTE: you must have a global var. N (vertex count) above this
//access a[i,j]
#define M(a,i,j) a[(i)*N+(j)]

const u4 NONE= 0xffffffff;

/*  The actual MCMF algorithm
 *  Args: <flow> - a pre-allocated NxN matrix, <cap> - the capacity matrix,
 *    <cost0> - the cost matrix (per unit flow), <s> - source vertex #, <t> - sink
 *  The flow matrix is stored in the provided array.
 */
void mcmf(i4 *flow, const u4 *cap, const i8 *cost0, u4 s, u4 t){
  //// Find an initial flow using Ford-Fulkerson
  u4 *res= (u4*)malloc(N*N*4);  //residual graph
  memcpy(res, cap, N*N*4);
  //note: to improve cache usage, cap[u][v] is replaced by flow[u][v]+res[u][v] from here on
  memset(flow, 0, N*N*4);       //start the flow empty
  u4 maxflow_amt=0;             //flow out of source
  //bottleneck capacity to each node; predecessor on the path to it
  u4 *cap_to= (u4*)malloc(N*4);
  u4 *parent= (u4*)malloc(N*4);
  loop{
    memset(cap_to, 0, N*4);
    memset(parent, 0x3f, N*4);
    deque<u4> Q;
    //BFS that chooses max capacity rather than min distance
    Q.push_back(s);
    cap_to[s]= INF;
    while(Q.size()){
      u4 u= Q.front();
      Q.pop_front();
      u4 cu= cap_to[u];
      for(u4 v=0; v<N; ++v){
        if(v==u || !M(res,u,v)) continue;
        u4 ncv= min(cu, M(res,u,v));   //the new path capacity to v
        if(ncv > cap_to[v]){
          cap_to[v]= ncv;
          parent[v]= u;
          Q.push_back(v);
        }
      }
    }
    //traverse the path and increase all flows by path capacity
    u4 path_cap= cap_to[t];
    if(!path_cap) break;     //no augmenting path => flow is max.
    //working backwards, add the path into the flow
    for(u4 v=t; v!=s;){
      u4 u= parent[v];
      M(flow,u,v) += path_cap;
      M(res,u,v) -= path_cap;
      M(flow,v,u) -= path_cap;
      M(res,v,u) += path_cap;
      v=u;
    }
    //flow out of source always increases
    maxflow_amt += path_cap;
  }
  free(parent);
  free(cap_to);
  D(printf("sizeof(res)= %u, maxflow %u -> %u (%u nodes) = %d\n", N*N*2, s, t, N, maxflow_amt));
  if(!maxflow_amt)
    return;

  //// Multiply cost by N so that the epsilon is reduced to 1 rather that 1/N
  i8 *cost= (i8*)malloc(N*N*8);
  i8 max_cost=0;
  for(u4 u=0; u<N; ++u){
    for(u4 v=0; v<N; ++v){
      M(cost,u,v)= M(cost0,u,v)*(i8)N;
      if(max_cost < M(cost,u,v)) max_cost= M(cost,u,v);
    }
  }
  i8 *price= (i8*)calloc(N, 8);   //start prices at 0
  i4 *ex= (i4*)malloc(N*4);     //excess flow into each node
  deque<u4> active;             //all u with e[u]>0
  //keep an adj. list for each vertex (based on res. graph); NONE means deleted entry
  vector<vector<u4> > adj(N, vector<u4>(0));
  //the position of each matrix entry in the adj. list (NONE= not in list)
  u4 *in_adj= (u4*)malloc(N*N*4);

  //// Main loop: refine from eps=max_cost to eps=1
  for(i8 eps= max_cost; eps>1;){
    memset(in_adj, 0xff, N*N*4);
    for(u4 u=0; u<N; ++u)
      adj[u].clear();
    //// Saturate all arcs of negative reduced cost
    //also do (non-speculative) arc fixing:
    //  delete an arc from the residual if abs(cp) > 2N*eps
    for(u4 u=0; u<N; ++u){
      for(u4 v=0; v<N; ++v){
        if(M(res,u,v)){
          i8 cp= M(cost,u,v) + price[u] - price[v];
          //abs() won't work with i8 arg
          i8 cp_max= 2*N*eps;
          if(cp > cp_max || cp < -cp_max){
            M(res,u,v)= 0;
            continue;
          }
          if(cp < 0){
            u4 c= M(res,u,v) + M(flow,u,v);   //capacity u->v
            M(res,v,u) += M(res,u,v);   //reverse residual must increase the same amount the forward res. decreased
            M(res,u,v)=0;
            M(flow,u,v)=c;
            M(flow,v,u)=-c;
            //record the reverse arc
            if(M(in_adj,v,u) == NONE){
              M(in_adj,v,u)= adj[v].size();
              adj[v].push_back(u);
            }
          }else if(M(in_adj,u,v) == NONE){  //record this arc's presence (since it wasn't saturated)
            M(in_adj,u,v)= adj[u].size();
            adj[u].push_back(v);
          }
        }
      }
    }
    //identify active nodes
    active.clear();
    memset(ex, 0, N*4);
    ex[s]= maxflow_amt;
    ex[t]= -maxflow_amt;
    for(u4 u=0; u<N; ++u){
      for(u4 v=0; v<N; ++v)
        ex[u] -= M(flow,u,v);
      if(ex[u] > 0)
        active.push_back(u);
    }
    //update eps. (which is not used above here)
    eps= (eps+4)/5;
    D(printf("eps=%llu:\n", eps));
    //// Discharge active nodes until there are none
    while(active.size()){
      u4 u= active.front();
      active.pop_front();
      D(printf("  discharge %u\n", u));
      //discharge: push/relabel until excess is removed
      u4 iv=0;                  //index in adj[u] of the next neighbor to push flow into
      u4 v= NONE;
      while(ex[u] > 0){
        for(; iv < adj[u].size(); ++iv){
          v= adj[u][iv];
          if(v == NONE) continue;
          if(M(res,u,v)) break;
        }
        if(iv == adj[u].size()){  //pushed to all neighbors, now relabel
          i8 max_p= -INF8;
          for(u4 iw=0; iw < adj[u].size(); ++iw){
            u4 w= adj[u][iw];
            if(w == NONE) continue;
            i8 p= price[w] - M(cost,u,w) - eps;
            if(max_p < p) max_p= p;
          }
          price[u]= max_p;
          D(printf("    relabel to %lld\n", max_p));
          for(iv=0; iv < adj[u].size(); ++iv){
            v= adj[u][iv];
            if(v == NONE) continue;
            if(M(res,u,v)) break;
          }
        }
        //push applies if capacity into v is available (ensured above) and reduced cost is <0
        if(M(cost,u,v) + price[u] - price[v] < 0){
          bool push_ok=true;
          //apply lookahead heuristic: don't push if e[v]>=0 and v has no admissible arcs
          if(ex[v] >= 0){
            push_ok=false;
            for(u4 iw=0; iw < adj[v].size(); ++iw){
              u4 w= adj[v][iw];
              if(w != NONE && M(cost,v,w) + price[v] - price[w] < 0){
                push_ok=true;
                break;
              }
            }
          }
          if(push_ok){
            D(printf("    push to %u\n", v));
            if(M(in_adj,v,u) == NONE){   //add reverse edge to flow net
              M(in_adj,v,u)= adj[v].size();
              adj[v].push_back(u);
            }
            //push as much as possible into v
            i4 delta= min(ex[u], (i4)M(res,u,v));
            M(flow,u,v) += delta;
            M(res,u,v) -= delta;
            M(flow,v,u) -= delta;
            M(res,v,u) += delta;
            if(!M(res,u,v)){   //remove forward edge from flow net
              adj[u][M(in_adj,u,v)]= NONE;
              M(in_adj,u,v)= NONE;
            }
            ex[u] -= delta;
            ex[v] += delta;
            if(ex[v] > 0 && ex[v] <= delta)   //v became active
              active.push_back(v);
          }else{                //relabel v
            i8 max_p= -INF8;
            for(u4 iw=0; iw < adj[v].size(); ++iw){
              u4 w= adj[v][iw];
              if(w == NONE) continue;
              i8 p= price[w] - M(cost,v,w) - eps;
              if(max_p < p) max_p= p;
            }
            if(max_p > -INF8)
              price[v]= max_p;
            else                //no residual arcs out of w
              price[v] -= eps;
            D(printf("    relabel %u to %lld\n", v, price[v]));
          }
        }
        ++iv;
      }
    }
  }
  free(in_adj); free(ex); free(price); free(cost); free(res);
}
