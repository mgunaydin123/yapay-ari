package minegunaydin.nesneler;

import minegunaydin.nesneler.Sehir;

import java.util.List;

public class Bee
{
    public int status;
    public List<Sehir> memoryPath;
    public double measureOfQuality;
    public int numberOfVisits;

    public Bee(int stat, List<Sehir>  path, double measure, int numVisits)
    {
        this.status = stat;
        this.memoryPath = path;
        this.measureOfQuality = measure;
        this.numberOfVisits = numVisits;
    }
}