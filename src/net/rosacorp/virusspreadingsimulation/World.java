package net.rosacorp.virusspreadingsimulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class World {

    private HashMap<Integer, Inhabitant> inhabitants;

    // Infected persons (this will be easier when we will spread the virus ;) )
    private ArrayList<Integer> infected;
    private ArrayList<Integer> lastInfected;

    // We put the dead persons here
    private ArrayList<Integer> deads;


    private int day;

    public World() {
        this.inhabitants = new HashMap<>();
        this.infected = new ArrayList<>();
        this.lastInfected = new ArrayList<>();
        this.deads = new ArrayList<>();
        this.day = 0;
    }

    // Generate all the Inhabitants
    public void generateInhabitants() {

        // long now = System.currentTimeMillis() / 1000l;

        for (int i = 0; i < VirusSpreadingSimulation.numberOfInhabitants; i++) {
            this.inhabitants.put(i, new Inhabitant(i));
        }

        //System.out.println(System.currentTimeMillis() / 1000l - now);

    }

    // Methods used to find the neighbors of a given Inhabitant
    public void findRelations() {

        for (Map.Entry<Integer, Inhabitant> entry1 : this.inhabitants.entrySet()) {
            for (Map.Entry<Integer, Inhabitant> entry2 : this.inhabitants.entrySet()) {

                // If the two entries are the same (the same persons we break, because of optimization)
                if (entry1.getKey() == entry2.getKey()) {
                    break;
                }

                // Else we continue and calculate the distance between the two persons
                double distance = entry1.getValue().getDistanceBetween(entry2.getValue());

                // If the distance is less than the radius we are looking for and this is not the current person
                if (distance < VirusSpreadingSimulation.radius && distance != 0) {

                    // We check is this person is not already in the "friend list" and that the friend list is not full
                    if (!entry1.getValue().getNeighbors().contains(entry2.getKey()) && entry1.getValue().getNeighbors().size() < VirusSpreadingSimulation.maxNumberOfFriends) {
                        entry1.getValue().getNeighbors().add(entry2.getKey());
                    }

                    // Hum can we do that ? I think so
                    if (!entry2.getValue().getNeighbors().contains(entry1.getKey()) && entry2.getValue().getNeighbors().size() < VirusSpreadingSimulation.maxNumberOfFriends) {
                        entry2.getValue().getNeighbors().add(entry1.getKey());
                    }
                }
            }
        }

        // Now we need to add a random person to the people he knows (we do it after not to set a person which is in the radius)
        for(Map.Entry<Integer, Inhabitant> entry : this.inhabitants.entrySet()) {

            // We take a random person (with its id) which is not the current person and not a person of the current person's neighbors
            Random random = new Random(System.currentTimeMillis() + new Random().nextInt());
            int personID;

            // We loop while the person id fulfil the two conditions
            do {
                personID = random.nextInt(VirusSpreadingSimulation.numberOfInhabitants);
            } while (entry.getKey() == personID || entry.getValue().getNeighbors().contains(personID));

            // Then we can add this friend the current person's list
            entry.getValue().getNeighbors().add(personID);
        }
    }

    // We add a whole day, that means we want the infected ones to spread the virus
    public void addOneDay() {
        this.day++;

        // Do we say that somebody infected today can also infect others the same day ? Not for now
        ArrayList<Integer> tempLastInfected = (ArrayList<Integer>) lastInfected.clone();

        // We reset the last infected arraylist (for the next day)
        this.lastInfected = new ArrayList<>();

        // We loop through all the last infected persons (not the real infected arraylist because they have already infected everybody)
        for(Integer holderID : tempLastInfected) {

            // Now we loop all their relations and spread the virus
            for (Integer friendID: this.getInhabitants().get(holderID).getNeighbors()) {

                // We first check that this person is not already infected (for optimization). And if not we contaminate him
                if (!this.getInfected().contains(friendID)) {

                    // We can infect the person
                    this.getInhabitants().get(friendID).setInfected();
                }
            }
        }

        // If we are at day 14 we can begin to know if people are dying or not from the virus


        // Give some stats
        System.out.println("Day " + this.day + ": " + this.infected.size() + " infected persons and " + this.lastInfected.size() + " today");
    }

    public HashMap<Integer, Inhabitant> getInhabitants() {
        return inhabitants;
    }

    public void setInhabitants(HashMap<Integer, Inhabitant> inhabitants) {
        this.inhabitants = inhabitants;
    }

    public ArrayList<Integer> getInfected() {
        return infected;
    }

    public void setInfected(ArrayList<Integer> infected) {
        this.infected = infected;
    }

    public ArrayList<Integer> getDeads() {
        return deads;
    }

    public void setDeads(ArrayList<Integer> deads) {
        this.deads = deads;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public ArrayList<Integer> getLastInfected() {
        return lastInfected;
    }

    public void setLastInfected(ArrayList<Integer> lastInfected) {
        this.lastInfected = lastInfected;
    }

}