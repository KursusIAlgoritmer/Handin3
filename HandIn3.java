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
    }

    static class StedTid implements Comparable<StedTid>{
      public static int antalKnuder = 0;
      private static MinPQ[] minPQliste;
      int knudeNummer,stationsNummer,tidspunkt;
      int afgangsTid, afgangsStation;

      private StedTid(int s, int t, int k, int at, int as){
        knudeNummer    = k;
        stationsNummer = s;
        tidspunkt      = t;
        afgangsTid     = at;
        afgangsStation = as;
      }
      public static void initOpslagStationStedTid(int antalAfgange){
        minPQliste = new MinPQ[antalAfgange];
        for(int i = 0; i < minPQliste.length ; i++)  minPQliste[i] = new MinPQ<StedTid>();
      }
      public static StedTid opret(int station, int tid, int knudeNr, int afgangsTid, int afgangsStation){
        antalKnuder++;
        StedTid st =  new StedTid(station,tid,knudeNr, afgangsTid, afgangsStation);
        minPQliste[station].insert(st);
        return st;
      }
      public static MinPQ<StedTid> hentSTer(int station){
        return (MinPQ<StedTid>)minPQliste[station];
      }
      public int compareTo(StedTid st) {
        return tidspunkt - st.tidspunkt;
      }
    }

    public static void main(String[] args) {
              In in = new In(args[0]);

              int antalStop       = in.readInt();
              int antalAfgange    = in.readInt();
              int antalForspg     = in.readInt();
              int hjemmeStation   = in.readInt();

              StedTid.initOpslagStationStedTid(antalAfgange);

              Digraph totalForbindelsesGraf = new Digraph(antalStop);

              //Del A : Opret stedTid for hjemmestationen
              StedTid.opret(hjemmeStation,0,0,0,0);

              //Del B: Opret stedTid for alle ankomster!
              for(int knudeNummer = 1; knudeNummer <= antalAfgange ; knudeNummer++){
                int startStation  = in.readInt();
                int stopStation   = in.readInt();
                int afgangsTid    = in.readInt();
                int ankomstTid    = in.readInt();

                totalForbindelsesGraf.addEdge(startStation,stopStation);

                StedTid.opret(stopStation,ankomstTid,knudeNummer, afgangsTid,startStation);
              }

            //Del C: Opret kant til denne stedTid fra alle stedTider hvor følgende gælder
            //hvis tidspunktet    <= afgangsTid og hvis stationsNummer = afgangStation
            for(int i = 0; i < antalStop ; i++){
              for(StedTid endeST : StedTid.hentSTer(i))
                for(StedTid startST: StedTid.hentSTer(endeST.afgangsStation)){
                  if(startST.tidspunkt <= endeST.afgangsTid){
                      Forbindelse.opret(startST,endeST);
                  }
                }
              }

              //Del D: Forspørgsler
              Digraph dagsForbindelsesGraf       = new Digraph(StedTid.antalKnuder);
              for(Forbindelse f : Forbindelse.liste) dagsForbindelsesGraf .addEdge(f.fra.knudeNummer,f.til.knudeNummer);
              DirectedDFS dagforbindelserDFS = new DirectedDFS(dagsForbindelsesGraf ,0); //det er hardcoded at vi starter altid på 0
              DirectedDFS totalForbindelsesDFS = new DirectedDFS(totalForbindelsesGraf,hjemmeStation); //her brugers stationer som knuder

              for(int i = 0 ; i < antalForspg ; i++){
                int station = in.readInt();

                int hurtigsteTid = -1;
                for(StedTid st : StedTid.hentSTer(station)){
                  if(dagforbindelserDFS.marked(st.knudeNummer)){
                    if(hurtigsteTid == -1 || hurtigsteTid > st.tidspunkt){
                      hurtigsteTid = st.tidspunkt;
                      break;
                    }
                  }
                }

                boolean erDerForbindelse = totalForbindelsesDFS.marked(station);

                if(!erDerForbindelse)         StdOut.println("Jeg køber sku en bil");
                else if( hurtigsteTid == -1)  StdOut.println("Ikke samme dag");
                else                          StdOut.println(hurtigsteTid);
              }
    }
}
