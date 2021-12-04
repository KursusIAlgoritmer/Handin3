public class HandIn3 {

    public static void main(String[] args) {

        In in = new In(args[0]);

        int v = in.readInt();

        Digraph G = new Digraph(v);

        for(int i = 1 ; i < v; i++ ){
          int p = in.readInt();
          G.addEdge(p, i);
        }

        int n = in.readInt();

        for(int j = 0; j < n; j++ ){
          String kommando = in.readString();
          int startKnude  = in.readInt();
          int slutKnude   = in.readInt();

          if(kommando.equals("vej")){
            Stack<Integer> vej = findKortestVejfraAtilB(startKnude,slutKnude,G);
            int antal = vej.size();
            for(int i= 0 ; i < antal; i++){
              if(i< antal-1)  StdOut.print(vej.pop() + "->");
              if(i==antal-1)  StdOut.println(vej.pop());
            }
          }
          if(kommando.equals("tid")){
              StdOut.println(beregningAfTid(startKnude,slutKnude,G));
          }

        }

    }

   private static int beregningAfTid(int startKnude, int slutKnude, Digraph rettetGraf){
        int tid = 0;
        int sidsteKnude = 0;
        for(int denneKnude : findKortestVejfraAtilB(startKnude,slutKnude,rettetGraf)){
           boolean tagetLift = sidsteKnude !=0 && denneKnude ==0;
           tid = tagetLift ? tid+5 : tid +1;
           sidsteKnude = denneKnude;
        }
        return tid-1;
    }

    private static Stack<Integer> findKortestVejfraAtilB(int startKnude, int slutKnude,Digraph rettetGraf ){
      Stack<Integer> shortestPath = new Stack<Integer>();

      //Direkte vej
      DepthFirstDirectedPaths dfs = new DepthFirstDirectedPaths(rettetGraf, startKnude);
      Stack<Integer> directPath = denDirekteVej(dfs,slutKnude);

      //Eller vej hurtigst fra startKnude til bund + vej fra top til slutKnude
      if(directPath.isEmpty()){
        pushFraStakTilStak( kortesteVejTil(dfs,findBundKnuder(rettetGraf)) ,                      directPath);
        pushFraStakTilStak( denDirekteVej(new DepthFirstDirectedPaths(rettetGraf, 0),slutKnude) , directPath);
        pushFraStakTilStak( directPath,                                                         shortestPath);
      }else{
        shortestPath = directPath;
      }

      return shortestPath;
    }

    private static void pushFraStakTilStak(Stack<Integer> knudeListeFra, Stack<Integer> knudeListeTil){
      for(int i : knudeListeFra) knudeListeTil.push(i);
    }

    private static Stack<Integer> kortesteVejTil(DepthFirstDirectedPaths dfs, Iterable<Integer> knudeListe) {
        Stack<Integer> kortesteVej = new Stack<Integer>();
        for(int knude : knudeListe){
            Stack newPath = (Stack) dfs.pathTo(knude);
            if((newPath != null) && (kortesteVej.size() == 0 || kortesteVej.size() > newPath.size())){
                kortesteVej = newPath;
            }
        }
        return kortesteVej;
    }

    private static Stack<Integer> findBundKnuder(Digraph G){
        Stack<Integer> bundKnuder = new Stack<Integer>();
        for (int v = 0; v < G.V(); v++) {
            if(((Bag)G.adj(v)).isEmpty())
                bundKnuder.push(v);
        }
        return bundKnuder;
    }

    private static Stack<Integer> denDirekteVej(DepthFirstDirectedPaths dfs, int v){
        if(dfs.hasPathTo(v)){
            return  (Stack)dfs.pathTo(v);
        }else{
            return new Stack<Integer>();
        }
    }

}
