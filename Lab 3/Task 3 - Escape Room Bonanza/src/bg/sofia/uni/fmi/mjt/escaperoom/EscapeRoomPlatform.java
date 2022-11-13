package bg.sofia.uni.fmi.mjt.escaperoom;

import bg.sofia.uni.fmi.mjt.escaperoom.exception.PlatformCapacityExceededException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.RoomAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.RoomNotFoundException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.TeamNotFoundException;
import bg.sofia.uni.fmi.mjt.escaperoom.room.EscapeRoom;
import bg.sofia.uni.fmi.mjt.escaperoom.room.Review;
import bg.sofia.uni.fmi.mjt.escaperoom.team.Team;

public class EscapeRoomPlatform implements EscapeRoomAdminAPI, EscapeRoomPortalAPI{

    private Team[] teams;
    private int maxCapacity;
    private int roomsCount;
    private EscapeRoom[] rooms;

    private boolean RoomExists(EscapeRoom room){
        return RoomExists(room.getName());
    }

    private boolean RoomExists(String roomName){
        for (int i = 0; i < this.roomsCount; i++) {
            EscapeRoom curRoom = this.rooms[i];
            if(curRoom.getName().equals(roomName)){
                return true;
            }
        }

        return false;
    }

    private boolean TeamExists(String teamName){
        boolean roomExists = false;
        for (int i = 0; i < this.roomsCount; i++) {
            Team curTeam = this.teams[i];
            if(curTeam.getName().equals(teamName)){
                return true;
            }
        }

        return false;
    }

    private boolean TeamExists(Team team){
        return this.TeamExists(team.getName());
    }

    private Team getTeamByName(String teamName) throws TeamNotFoundException {
        for (int i = 0; i < this.teams.length; i++) {
            if(this.teams[i].getName().equals(teamName)) return this.teams[i];
        }

        throw new TeamNotFoundException("A team with name " + teamName + " doesn't exist");
    }

    public EscapeRoomPlatform(Team[] teams, int maxCapacity){
        this.teams = teams;
        this.maxCapacity = maxCapacity;
        this.rooms = new EscapeRoom[maxCapacity];
        this.roomsCount = 0;
    }

    @Override
    public void addEscapeRoom(EscapeRoom room) throws RoomAlreadyExistsException {
        if(room == null) throw new IllegalArgumentException("The room is null");
        if(this.roomsCount == this.maxCapacity) throw new PlatformCapacityExceededException("The platform already has the maximum number of rooms added");

        if(RoomExists(room)) throw new RoomAlreadyExistsException("The room with name " + room.getName() + " already exists");

        this.rooms[this.roomsCount++] = room;
    }

    @Override
    public void removeEscapeRoom(String roomName) throws RoomNotFoundException {
        if(roomName == null || roomName.isBlank()) throw new IllegalArgumentException("The roomName is null, empty or blank");
        if(!RoomExists(roomName)) throw new RoomNotFoundException("A room with name " + roomName + " doesn't exist");

        boolean roomFound = false;
        for (int i = 0; i < this.roomsCount; i++) {
            if(roomFound) this.rooms[i - 1] = this.rooms[i];

            if(this.rooms[i].getName().equals(roomName)){
                roomFound = true;
            }
        }

        //Remove the last room
        this.rooms[--this.roomsCount] = null;
    }

    @Override
    public EscapeRoom[] getAllEscapeRooms() {

        EscapeRoom[] escapeRooms = new EscapeRoom[this.roomsCount];
        for (int i = 0; i < this.roomsCount; i++) {
            escapeRooms[i] = this.rooms[i];
        }

        return escapeRooms;
    }

    @Override
    public void registerAchievement(String roomName, String teamName, int escapeTime) throws RoomNotFoundException, TeamNotFoundException {
        if(roomName == null || roomName.isBlank()) throw new IllegalArgumentException("The roomName is null, empty or blank");
        if(teamName == null || teamName.isBlank()) throw new IllegalArgumentException("The teamName is null, empty or blank");

        if(!RoomExists(roomName)) throw new RoomNotFoundException("A room with name " + roomName + " doesn't exist");
        if(!TeamExists(teamName)) throw new TeamNotFoundException("A team with name " + teamName + " doesn't exist");

        EscapeRoom room = this.getEscapeRoomByName(roomName);
        if(escapeTime <= 0 || escapeTime > room.getMaxTimeToEscape()) throw new IllegalArgumentException("The escape time of the team is bigger than the maximum for the room");

        Team curTeam = this.getTeamByName(teamName);
        curTeam.updateRating(room.getDifficulty().getRank());

        if(escapeTime * 2 <= room.getMaxTimeToEscape()) curTeam.updateRating(2);
        else if(escapeTime * 4 <= room.getMaxTimeToEscape() * 3) curTeam.updateRating(1);

    }

    @Override
    public EscapeRoom getEscapeRoomByName(String roomName) throws RoomNotFoundException {
        if(roomName == null || roomName.isBlank()) throw new IllegalArgumentException("The roomName is null, empty or blank");
        if(!this.RoomExists(roomName)) throw new RoomNotFoundException("A room with name " + roomName + " doesn't exist");

        for (int i = 0; i < this.roomsCount; i++) {
            if(this.rooms[i].getName().equals(roomName)) return this.rooms[i];
        }

        return null;
    }

    @Override
    public void reviewEscapeRoom(String roomName, Review review) throws RoomNotFoundException {
        if(roomName == null || roomName.isBlank()) throw new IllegalArgumentException("The roomName is null, empty or blank");
        if(review == null) throw new IllegalArgumentException("The review is null");
        if(!this.RoomExists(roomName)) throw new RoomNotFoundException("A room with name " + roomName + " doesn't exist");

        EscapeRoom room = this.getEscapeRoomByName(roomName);
        room.addReview(review);
    }

    @Override
    public Review[] getReviews(String roomName) throws RoomNotFoundException {
        if(roomName == null || roomName.isBlank()) throw new IllegalArgumentException("The roomName is null, empty or blank");
        if(!this.RoomExists(roomName)) throw new RoomNotFoundException("A room with name " + roomName + " doesn't exist");

        EscapeRoom room = this.getEscapeRoomByName(roomName);

        if(room.getReviewsCountTotal() == 0) return new Review[0];

        return room.getReviews();
    }

    @Override
    public Team getTopTeamByRating() {

        if(this.teams.length == 0) return null;

        Team highestTeam = null;

        for (int i = 0; i < this.teams.length; i++) {
            Team curTeam = this.teams[i];

            if(highestTeam == null || highestTeam.getRating() < curTeam.getRating()) highestTeam = curTeam;
        }

        return highestTeam;
    }
}
