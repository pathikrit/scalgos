#include <iostream>
#include <vector>
#include <string>
#include <cstdlib>
using namespace std;

long long below(long long a,long long m,bool include);

int main(){
  int a,b,m;
  while(cin>>a>>b>>m && m){
    if(a>b) swap(a,b);
    long long low=below(a,m,false);
    long long high=below(b,m,true);
    if(a<=0 && b>=0)
      high++;
    cout<<high-low<<'\n';
  }
}

long long below(long long a,long long m,bool include){
  if(a<0)
    return -below(-a,m,!include);
  if(a==0)
    return 0;
  if(include)
    return a/abs(m);
  return (a-1)/abs(m);
}
