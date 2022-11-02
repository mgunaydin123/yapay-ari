package minegunaydin.nesneler;


import java.util.List;

public interface UIEvents {

    void enKısaYoluCiz(List<Sehir> particle, double distance);
    void enUzunMesafeCiz(Double distance);

    void simulasyonTamamlandi(SIMULATION_RESULT result);

   public  enum SIMULATION_RESULT{
        MAX_ITERASYON,
        BEST_VALUE
    }
}
