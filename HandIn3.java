import java.util.*;

public class HandIn3 {

    static class Forbindelse{
      public static ArrayList<Forbindelse> liste = new ArrayList<Forbindelse>();
      BusStop fra, til;

      private Forbindelse(BusStop a, BusStop b){
        fra = a; til = b;
      }
      public static void opret(BusStop a, BusStop b){
        liste.add(new Forbindelse(a,b));
      }
    }

    static class BusStop implements Comparable<BusStop>{
      public static int antalKnuder = 0;
      private static MinPQ[] minPQliste;
      int knudeNummer,stationsNummer,tidspunkt;
      int afgangsTid, afgangsStation;

      private BusStop(int s, int t, int k, int at, int as){
        knudeNummer    = k;
        stationsNummer = s;
        tidspunkt      = t;
        afgangsTid     = at;
        afgangsStation = as;
      }
      public static void initOpslagStationBusStop(int antalAfgange){
        minPQliste = new MinPQ[antalAfgange];
        for(int i = 0; i < minPQliste.length ; i++)  minPQliste[i] = new MinPQ<BusStop>();
      }
      public static BusStop opret(int station, int tid, int knudeNr, int afgangsTid, int afgangsStation){
        antalKnuder++;
        BusStop st =  new BusStop(station,tid,knudeNr, afgangsTid, afgangsStation);
        minPQliste[station].insert(st);
        return st;
      }
      public static MinPQ<BusStop> hentBS(int station){
        return (MinPQ<BusStop>)minPQliste[station];
      }
      public int compareTo(BusStop st) {
        return tidspunkt - st.tidspunkt;
      }
    }

    public static void main(String[] args) {
              In in = new In(args[0]);

              int antalStop       = in.readInt();
              int antalAfgange    = in.readInt();
              int antalForspg     = in.readInt();
              int hjemmeStation   = in.readInt();

              BusStop.initOpslagStationBusStop(antalAfgange);

              Digraph totalGraf = new Digraph(antalStop);

              //Del A : Opret stedTid for hjemmestationen
              BusStop.opret(hjemmeStation,0,0,0,0);

              //Del B: Opret stedTid for alle ankomster!
              for(int knudeNummer = 1; knudeNummer <= antalAfgange ; knudeNummer++){
                int startStation  = in.readInt();
                int stopStation   = in.readInt();
                int afgangsTid    = in.readInt();
                int ankomstTid    = in.readInt();

                totalGraf.addEdge(startStation,stopStation);

                BusStop.opret(stopStation,ankomstTid,knudeNummer, afgangsTid,startStation);
              }

            //Del C: Opret kant til denne stedTid fra alle stedTider hvor følgende gælder
            //hvis tidspunktet    <= afgangsTid og hvis stationsNummer = afgangStation
            for(int i = 0; i < antalStop ; i++){
              for(BusStop endeST : BusStop.hentBS(i))
                for(BusStop startST: BusStop.hentBS(endeST.afgangsStation)){
                  if(startST.tidspunkt <= endeST.afgangsTid){
                      Forbindelse.opret(startST,endeST);
                  }
                }
              }

              //Del D: Forspørgsler
              Digraph dagsGraf       = new Digraph(BusStop.antalKnuder);
              for(Forbindelse f : Forbindelse.liste) dagsGraf .addEdge(f.fra.knudeNummer,f.til.knudeNummer);
              DirectedDFS dagsDFS = new DirectedDFS(dagsGraf ,0); //det er hardcoded at vi starter altid på 0
              DirectedDFS totalDFS = new DirectedDFS(totalGraf,hjemmeStation); //her brugers stationer som knuder

              for(int i = 0 ; i < antalForspg ; i++){
                int station = in.readInt();

                int hurtigsteTid = -1;
                for(BusStop st : BusStop.hentBS(station)){
                  if(dagsDFS.marked(st.knudeNummer)){
                    if(hurtigsteTid == -1 || hurtigsteTid > st.tidspunkt){
                      hurtigsteTid = st.tidspunkt;
                      break;
                    }
                  }
                }

                boolean erDerForbindelse = totalDFS.marked(station);

                if(!erDerForbindelse)         StdOut.println("Jeg køber sku en bil");
                else if( hurtigsteTid == -1)  StdOut.println("Ikke samme dag");
                else                          StdOut.println(hurtigsteTid);
              }
    }
}
