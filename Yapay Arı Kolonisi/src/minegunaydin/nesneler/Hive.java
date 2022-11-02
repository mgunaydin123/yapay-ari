package minegunaydin.nesneler;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Hive extends JPanel implements Runnable{

    SIMULATION_STATUS simulation_status = SIMULATION_STATUS.STOPPED;

    UIEvents uıEvent;

    Random rand = new Random(0);

    List<Sehir> sehirler = SehirSaglayici.getInstance().getSehirler();

    int totalNumBees;
    int numberInactive;
    int numberActive;
    int numberScout;

    int maxNumberVisits;
    int maxNumberCycles;
    int totalCycle = 0;
    double bestLimit;
    double worst = -1;


    double probPersuasion = 0.90;
    double probMistake = 0.01;


    Bee[] bees;
    List<Sehir> bestMemoryPath;
    double bestMeasureOfQuality;
    int[] indexesOfInactiveBees;
    int neighborSwitches;

    public void setConfiguration(int totalBees, int numInactive, int numActive, int numScout,
                int maxCycles, int maxVisits, double bestLimit, UIEvents uıEvent)
    {
        this.bestLimit = bestLimit;
        this.uıEvent = uıEvent;
        this.totalNumBees = totalBees;
        this.numberInactive = numInactive;
        this.numberActive = numActive;
        this.numberScout = numScout;
        this.maxNumberCycles = maxCycles;
        this.maxNumberVisits = maxVisits;

        this.neighborSwitches = (sehirler.size() / 10) + 1;

        this.bees = new Bee[totalBees];


        this.bestMemoryPath = GenerateRandomPath();
        this.bestMeasureOfQuality = MeasureOfQuality(this.bestMemoryPath);

        this.indexesOfInactiveBees = new int[numberInactive];

        for (int i = 0; i < this.totalNumBees; i++)
        {
            int currStatus;
            if (i < numberInactive)
            {
                currStatus = 0;
                indexesOfInactiveBees[i] = i;
            }
            else if (i < numberInactive + numberScout)
            {
                currStatus = 2;
            }
            else
            {
                currStatus = 1;
            }

            List<Sehir> randomPath = GenerateRandomPath();
            double mq = MeasureOfQuality(randomPath);

            this.bees[i] = new Bee(currStatus, randomPath, mq, 0);

            if (bees[i].measureOfQuality < this.bestMeasureOfQuality)
            {
                this.bestMemoryPath.clear();
                this.bestMemoryPath.addAll(bees[i].memoryPath);
                this.bestMeasureOfQuality = this.bees[i].measureOfQuality;
            }
        }
        totalCycle = 0;
        List<Sehir> bestMemoryPath = new ArrayList<>();
        double bestMeasureOfQuality = Integer.MAX_VALUE;
    }




    private List<Sehir> GenerateRandomPath()
    {
      return SehirSaglayici.getInstance().rastgeleSiralamaListesiGetir();
    }

    private List<Sehir> GenerateNeighborPath(List<Sehir> path)
    {
        List<Sehir> result = new ArrayList<>(path);
        for (int i = 0; i < rand.nextInt(4) + 1; i++)
        {
            int ranIndex = rand.nextInt(result.size());
            int adjIndex;
            if (ranIndex == result.size() - 1)
                adjIndex = 0;
            else
                adjIndex = ranIndex + 1;

            Collections.swap(result, ranIndex, adjIndex);
        }

        return result;
    }


    private double MeasureOfQuality(List<Sehir> sehirler)
    {
        double answer = 0.0;
        for (int i = 0; i < sehirler.size() - 1; i++)
        {
            Sehir sehir = sehirler.get(i);
            Sehir sonrakiSehir = sehirler.get(i + 1);
            double d = SehirSaglayici.getInstance().sehirlerArasiMesafeHesapla(sehir, sonrakiSehir);
            answer += d;
        }
        answer += SehirSaglayici.getInstance().sehirlerArasiMesafeHesapla(sehirler.get(sehirler.size() - 1), sehirler.get(0));
        return answer;
    }

    public void Solve()
    {
        for (int i = 0; i < this.bees.length; i++)
        {
            if (this.bees[i].status == 1)
            {
                ProcessActiveBee(i);
            }
            else if (this.bees[i].status == 2)
            {
                ProcessScoutBee(i);
            }
            else if (this.bees[i].status == 0)
            {

            }
        }
    }


    private void ProcessActiveBee(int i)
    {
        List<Sehir> neighbor = GenerateNeighborPath(this.bees[i].memoryPath);
        double neighborQual = MeasureOfQuality(neighbor);
        double prob = rand.nextDouble();
        boolean pathWasUpdated = false;
        boolean numVisitsOverLimit = false;

        if (neighborQual < this.bees[i].measureOfQuality)
        {
            if (prob < this.probMistake)
            {
                ++this.bees[i].numberOfVisits;
                if (this.bees[i].numberOfVisits > this.maxNumberVisits)
                    numVisitsOverLimit = true;
            }
            else
            {
                this.bees[i].memoryPath = new ArrayList<>(neighbor);
                this.bees[i].measureOfQuality = neighborQual;
                this.bees[i].numberOfVisits = 0;
                pathWasUpdated = true;
            }
        }
        else
        {
            if (prob < this.probMistake)
            {
                this.bees[i].memoryPath = new ArrayList<>(neighbor);
                this.bees[i].measureOfQuality = neighborQual;
                this.bees[i].numberOfVisits = 0;
                pathWasUpdated = true;
            }
            else
            {
                ++this.bees[i].numberOfVisits;
                if (this.bees[i].numberOfVisits > this.maxNumberVisits)
                    numVisitsOverLimit = true;
            }
        }

        if (numVisitsOverLimit)
        {
            this.bees[i].status = 0;
            this.bees[i].numberOfVisits = 0;
            int x = rand.nextInt(this.numberInactive);
            this.bees[this.indexesOfInactiveBees[x]].status = 1;
            this.bees[this.indexesOfInactiveBees[x]].numberOfVisits = 0;
            indexesOfInactiveBees[x] = i;
        }
        else if (pathWasUpdated)
        {
            if (this.bees[i].measureOfQuality < this.bestMeasureOfQuality)
            {
                this.bestMemoryPath = new ArrayList<>(this.bees[i].memoryPath);
                this.bestMeasureOfQuality = this.bees[i].measureOfQuality;
                uıEvent.enKısaYoluCiz(bestMemoryPath, bestMeasureOfQuality);

                if(bestMeasureOfQuality <= bestLimit){
                    simulation_status = SIMULATION_STATUS.FINISHED;
                    uıEvent.simulasyonTamamlandi(UIEvents.SIMULATION_RESULT.BEST_VALUE);
                }

            }else if(this.bees[i].measureOfQuality > worst){
                worst = this.bees[i].measureOfQuality;
                uıEvent.enUzunMesafeCiz(worst);
            }
            DoWaggleDance(i);
        }
        else
        {
            return;
        }
    }


    private void ProcessScoutBee(int i)
    {
        List<Sehir> randomFoodSource = GenerateRandomPath();
        double randomQuality = MeasureOfQuality(randomFoodSource);
        if (randomQuality < this.bees[i].measureOfQuality)
        {
            this.bees[i].memoryPath = new ArrayList<>(randomFoodSource);
            this.bees[i].measureOfQuality = randomQuality;
            if (this.bees[i].measureOfQuality < this.bestMeasureOfQuality)
            {
                this.bestMemoryPath = new ArrayList<>(this.bees[i].memoryPath);
                this.bestMeasureOfQuality = this.bees[i].measureOfQuality;
                uıEvent.enKısaYoluCiz(bestMemoryPath, bestMeasureOfQuality);

                if(bestMeasureOfQuality <= bestLimit){
                    simulation_status = SIMULATION_STATUS.FINISHED;
                    uıEvent.simulasyonTamamlandi(UIEvents.SIMULATION_RESULT.BEST_VALUE);
                }
            }else if(this.bees[i].measureOfQuality > worst){
                worst = this.bees[i].measureOfQuality;
                uıEvent.enUzunMesafeCiz(worst);
            }
            DoWaggleDance(i);
        }
    }


    private void DoWaggleDance(int i)
    {
        for (int x = 0; x < this.numberInactive; x++)
        {
            int b = this.indexesOfInactiveBees[x];
            if (this.bees[i].measureOfQuality < this.bees[b].measureOfQuality)
            {
                double p = rand.nextDouble();
                if (this.probPersuasion > p)
                {
                    this.bees[b].memoryPath = new ArrayList<>(this.bees[i].memoryPath);
                    this.bees[b].measureOfQuality = this.bees[i].measureOfQuality;
                }
            }
        }
    }


    @Override
    public void paintComponent(Graphics gfx) {
        super.paintComponent(gfx);
        if(!refresh || simulation_status != SIMULATION_STATUS.RUNNING) return;
        refresh = false;
        if(totalCycle < maxNumberCycles){
            Solve();
            totalCycle++;
        }else{
            simulation_status = SIMULATION_STATUS.FINISHED;
            uıEvent.simulasyonTamamlandi(UIEvents.SIMULATION_RESULT.MAX_ITERASYON);
        }
        sehirleriCiz(gfx);



    }

    public void drawParticule(Graphics gfx, List<Sehir> bestMemoryPath){

        for(int i = 0; i < bestMemoryPath.size(); i++){
            Sehir sehir = bestMemoryPath.get(i);
            sehir.goster(gfx);
        }

        gfx.setColor(Color.WHITE);
        for(int i = 0; i < bestMemoryPath.size() - 1; i++)
        {
            Sehir sehir = bestMemoryPath.get(i);
            Sehir sonrakiSehir = bestMemoryPath.get(i + 1);
            gfx.drawLine((int) (sehir.getX() + sehir.getDaireBoyutu() / 2), (int)(sehir.getY() + sehir.getDaireBoyutu() / 2),
                    (int) (sonrakiSehir.getX() + sehir.getDaireBoyutu() /2), (int) (sonrakiSehir.getY() + sehir.getDaireBoyutu() / 2));
        }

        gfx.drawLine((int) (bestMemoryPath.get(bestMemoryPath.size() - 1).getX() + SehirSaglayici.getInstance().getSehirler().get(0).getDaireBoyutu() / 2), (int)(bestMemoryPath.get(bestMemoryPath.size() - 1).getY() + SehirSaglayici.getInstance().getSehirler().get(0).getDaireBoyutu() / 2),
                (int) (bestMemoryPath.get(0).getX()  + SehirSaglayici.getInstance().getSehirler().get(0).getDaireBoyutu() / 2), (int) (bestMemoryPath.get(0).getY()+ SehirSaglayici.getInstance().getSehirler().get(0).getDaireBoyutu() / 2));
    }

    public void sehirleriCiz(Graphics gfx){
        gfx.setColor(new Color(0, 50, 80));
        gfx.fillRect(0, 0, App.CANVAS_GENISLIK, App.CANVAS_YUKSEKLIK);


        drawParticule(gfx, bestMemoryPath);

        gfx.translate(App.CERCEVE_GENISLIK / 2, 0);

        drawParticule(gfx, SehirSaglayici.getInstance().rastgeleSiralamaListesiGetir());

        gfx.translate(-App.CERCEVE_GENISLIK / 2,  0);

    }

    public Hive(){
        new Thread(this).start();
    }

    public void simulasyonuBaslat(){
        simulation_status = SIMULATION_STATUS.RUNNING;
    }

    public void simulasyonDurdur(){
        simulation_status = SIMULATION_STATUS.STOPPED;
    }

    public boolean isStarted(){
        return simulation_status == SIMULATION_STATUS.RUNNING;
    }


    public enum SIMULATION_STATUS{
        STOPPED, RUNNING, FINISHED
    }

    boolean refresh = false;
    @Override
    public void run() {
        while (true) {
            if(simulation_status == SIMULATION_STATUS.RUNNING){
                if(!refresh) refresh = true;
                repaint();
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
