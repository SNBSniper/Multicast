import java.io.Serializable;

/**
 * Created by danielftapiar on 9/20/16.
 */
public class Distribumon implements Serializable{
    double id;
    String name;
    int level;

    public Distribumon(double random, String distribumonName, String distribumonLevel) {
        this.id = random;
        this.name = distribumonName;
        this.level = Integer.parseInt(distribumonLevel);
    }


    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
