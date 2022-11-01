package bg.sofia.uni.fmi.mjt.escaperoom.team;

import bg.sofia.uni.fmi.mjt.escaperoom.rating.Ratable;

public class Team implements Ratable {
    private TeamMember[] members = null;
    private String name = null;
    private double rating;

//    private Team(){
//
//    }

    private Team(String name, TeamMember[] members){
        this.members = members;
        this.name = name;
    }

    public static Team of(String name, TeamMember[] members){
        return new Team(name, members);
    }

    /**
     * Updates the team rating by adding the specified points to it.
     *
     * @param points the points to be added to the team rating.
     * @throws IllegalArgumentException if the points are negative.
     */
    public void updateRating(int points) {
        // TODO: add implementation here
        if(points < 0) throw new IllegalArgumentException("The points are less than 0");

        this.rating += points;
    }

    /**
     * Returns the team name.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public double getRating() {
        return this.rating;
    }
}
