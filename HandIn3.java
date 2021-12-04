import java.util.*;

public class HandIn3 {

    static class Forbindelse{
      public static ArrayList<Forbindelse> liste = new ArrayList<Forbindelse>();
      StedTid fra, til;

      private Forbindelse(StedTid a, StedTid b){
        fra = a; til = b;
      }
      public static void opret(StedTid a, StedTid b){
        liste.add(new Forbindelse(a,b));
      }
      public String toString(){
        return fra + "->" + til;
      }
    }

    static class StedTid implements Comparable<StedTid>{
      public static ArrayList<StedTid> liste = new ArrayList<StedTid>();
      int knudeNummer,stationsNummer,tidspunkt;

      private StedTid(int s, int t, int k){
        knudeNummer    = k;
        stationsNummer = s;
        tidspunkt      = t;
      }
      public int compareTo(StedTid stedTid) {
        return stationsNummer - stedTid.stationsNummer;
      }
      public String toString(){
        return "( Station: " + stationsNummer + ", Tidspunkt : " + tidspunkt + " , Knude :" + knudeNummer + ")";
      }
      public static StedTid opret(int station, int tid, int knude){
        StedTid st = new StedTid(station, tid, knude);
        liste.add(st);
        return st;
      }
    }

    public static void main(String[] args) {
              In in = new In(args[0]);

              int antalStop       = in.readInt();
              int antalAfgange    = in.readInt();
              int antalForspg     = in.readInt();
              int hjemmeStation   = in.readInt();

              //Del A : Opret stedTid for hjemmestationen
              StedTid.opret(hjemmeStation,0,0);

              //Del B: Opret stedTid for alle ankomster!
              for(int knudeNummer = 1; knudeNummer <= antalAfgange ; knudeNummer++){
                int startStation  = in.readInt();
                int stopStation   = in.readInt(); // sted
                int afgangsTid    = in.readInt();
                int ankomstTid    = in.readInt(); // tid

                StedTid endeST = StedTid.opret(stopStation,ankomstTid,knudeNummer);
                //Del C: Opret kant til denne stedTid fra alle stedTider hvor følgende gælder
                //  hvis tidspunktet    <= afgangsTid
                // hvis stationsNummer   = startStation
                for(StedTid startST: StedTid.liste){
                  if(startST.stationsNummer == startStation && startST.tidspunkt <= afgangsTid){
                      Forbindelse.opret(startST,endeST);
                  }
                }
              }

              Digraph G = new Digraph(StedTid.liste.size());

              for(Forbindelse f : Forbindelse.liste)
                G.addEdge(f.fra.knudeNummer,f.til.knudeNummer);

              DirectedDFS dfs = new DirectedDFS(G,hjemmeStation);

              for(int forsprg = 0; forsprg < antalForspg ; forsprg++){
                int inputStation  = in.readInt();

                if(dfs.marked(inputStation)){
                  int tid = -1;
                  for(StedTid st: StedTid.liste){
                    if(st.stationsNummer ==  inputStation){
                        if(tid == -1 || tid > st.tidspunkt) tid = st.tidspunkt;
                    }
                  }
                  StdOut.println(tid);
                }else{
                  StdOut.println("Umuligt!");
                }


              }

    }

}
