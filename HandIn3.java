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
      public String toString(){return fra + "->" + til;}
    }

    static class StedTid{
      public static int antalKnuder = 0;
      public static ArrayList[] stKeyLister;
      int knudeNummer,stationsNummer,tidspunkt;

      public StedTid(int s, int t, int k){
        knudeNummer    = k;
        stationsNummer = s;
        tidspunkt      = t;
      }
      public static void initOpslagStationStedTid(int antalAfgange){
        stKeyLister = new ArrayList[antalAfgange];
        for(int i = 0; i < stKeyLister.length ; i++)  stKeyLister[i] = new ArrayList<StedTid>();
      }
      public static StedTid opret(int station, int tid, int knudeNr){
        antalKnuder++;
        StedTid st =  new StedTid(station,tid,knudeNr);
        stKeyLister[station].add(st);
        return st;
      }
      public static ArrayList<StedTid> hentSTer(int station){
        return (ArrayList<StedTid>)stKeyLister[station];
      }
      public String toString(){return "( Station: "+stationsNummer+",tid: "+tidspunkt+",Knude :"+knudeNummer+")";}
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
              StedTid.opret(hjemmeStation,0,0);

              //Del B: Opret stedTid for alle ankomster!
              for(int knudeNummer = 1; knudeNummer <= antalAfgange ; knudeNummer++){
                int startStation  = in.readInt();
                int stopStation   = in.readInt(); // sted
                int afgangsTid    = in.readInt();
                int ankomstTid    = in.readInt(); // tid

                totalForbindelsesGraf.addEdge(startStation,stopStation);

                StedTid endeST = StedTid.opret(stopStation,ankomstTid,knudeNummer);
                //Del C: Opret kant til denne stedTid fra alle stedTider hvor følgende gælder
                //  hvis tidspunktet    <= afgangsTid og hvis stationsNummer   = startStation
                for(StedTid startST: StedTid.hentSTer(startStation)){
                  if(startST.tidspunkt <= afgangsTid){
                      Forbindelse.opret(startST,endeST);
                  }
                }
              }

              //for(StedTid st : StedTid.liste) StdOut.println(st);
              //for(Forbindelse f : Forbindelse.liste) StdOut.println(f);

              //Del D: Oprettelse af den rettede graf
              Digraph dagsForbindelsesGraf       = new Digraph(StedTid.antalKnuder);
              for(Forbindelse f : Forbindelse.liste) dagsForbindelsesGraf .addEdge(f.fra.knudeNummer,f.til.knudeNummer);
              DirectedDFS dagforbindelserDFS = new DirectedDFS(dagsForbindelsesGraf ,0); //hardcoded at vi starter altid på 0

              DirectedDFS totalForbindelsesDFS = new DirectedDFS(totalForbindelsesGraf,hjemmeStation); //her brugers stationer som knuder

              for(int i = 0 ; i < antalForspg ; i++){
                int station = in.readInt();

                ArrayList<StedTid> ankomstListe = StedTid.hentSTer(station);

                int hurtigsteTid = -1;
                for(StedTid st : StedTid.hentSTer(station)){
                  if(dagforbindelserDFS.marked(st.knudeNummer)){
                    if(hurtigsteTid == -1 || hurtigsteTid > st.tidspunkt) hurtigsteTid = st.tidspunkt;
                  }
                }

                boolean erDerForbindelse = totalForbindelsesDFS.marked(station);

                if(!erDerForbindelse)         StdOut.println("Jeg køber sku en bil");
                else if( hurtigsteTid == -1)  StdOut.println("Ikke samme dag");
                else                          StdOut.println(hurtigsteTid);
              }
    }
}
