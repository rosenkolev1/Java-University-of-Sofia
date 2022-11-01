import bg.sofia.uni.fmi.mjt.escaperoom.EscapeRoomPlatform;
import bg.sofia.uni.fmi.mjt.escaperoom.room.Difficulty;
import bg.sofia.uni.fmi.mjt.escaperoom.room.EscapeRoom;
import bg.sofia.uni.fmi.mjt.escaperoom.room.Review;
import bg.sofia.uni.fmi.mjt.escaperoom.room.Theme;
import bg.sofia.uni.fmi.mjt.escaperoom.team.Team;
import bg.sofia.uni.fmi.mjt.escaperoom.team.TeamMember;

import java.time.LocalDateTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) {
        TeamMember[] membersTeam1 = new TeamMember[]{
                new TeamMember("Vasil", LocalDateTime.of(2002, Month.JULY, 17, 12, 0)),
                new TeamMember("Ralitsa", LocalDateTime.of(2002, Month.JANUARY, 21, 12, 0)),
                new TeamMember("Yoana", LocalDateTime.of(2002, Month.SEPTEMBER, 10, 12, 0)),
        };

        TeamMember[] membersTeam2 = new TeamMember[]{
                new TeamMember("Roskata", LocalDateTime.of(2002, Month.SEPTEMBER, 17, 12, 0)),
                new TeamMember("Georgi", LocalDateTime.of(2002, Month.JULY, 6, 12, 0)),
                new TeamMember("Miro", LocalDateTime.of(2002, Month.DECEMBER, 27, 12, 0)),
        };

        TeamMember[] membersTeam3 = new TeamMember[]{
                new TeamMember("Pesho", LocalDateTime.of(2001, Month.JUNE, 11, 12, 0)),
                new TeamMember("Lubo", LocalDateTime.of(2001, Month.JULY, 9, 12, 0)),
                new TeamMember("Beti", LocalDateTime.of(2001, Month.JANUARY, 22, 12, 0)),
                new TeamMember("Acho", LocalDateTime.of(2001, Month.OCTOBER, 9, 12, 0)),
        };

        TeamMember[] membersTeam4 = new TeamMember[]{
                new TeamMember("Rado", LocalDateTime.of(2002, Month.OCTOBER, 13, 12, 0)),
                new TeamMember("Kiretu99", LocalDateTime.of(1999, Month.JULY, 4, 12, 0)),
                new TeamMember("Dankata", LocalDateTime.of(2002, Month.OCTOBER, 21, 12, 0)),
        };

        TeamMember[] membersTeam5 = new TeamMember[]{
                new TeamMember("Bilyana", LocalDateTime.of(2002, Month.OCTOBER, 5, 12, 0)),
                new TeamMember("Tonito", LocalDateTime.of(2002, Month.JULY, 25, 12, 0)),
                new TeamMember("Shefkata", LocalDateTime.of(2002, Month.MAY, 14, 12, 0)),
        };

        TeamMember[] membersTeam6 = new TeamMember[]{
                new TeamMember("Tedo", LocalDateTime.of(2002, Month.AUGUST, 7, 12, 0)),
                new TeamMember("Ilko", LocalDateTime.of(2002, Month.AUGUST, 24, 12, 0)),
        };

        Team[] teams = new Team[]{
                Team.of("Yoanite", membersTeam1),
                Team.of("Burgaskite Batki", membersTeam2),
                Team.of("Rosen Rescue Squad", membersTeam3),
                Team.of("Izvun-StudentskoGradnite", membersTeam4),
                Team.of("Rusensko Vareno", membersTeam5),
                Team.of("Gegite", membersTeam6),
        };

        EscapeRoomPlatform plat = new EscapeRoomPlatform(teams, 5);

        EscapeRoom er1 = new EscapeRoom("Stenata na pla4a", Theme.MYSTERY, Difficulty.HARD, 50, 25, 4);
        EscapeRoom er2 = new EscapeRoom("Lambdata na Trifon", Theme.SCIFI, Difficulty.EXTREME, 60, 30, 3);
        EscapeRoom er3 = new EscapeRoom("Piqn Vasilii", Theme.HORROR, Difficulty.EXTREME, 60, 30, 2);
        EscapeRoom er4 = new EscapeRoom("Popravka pri Borko", Theme.FANTASY, Difficulty.MEDIUM, 30, 15, 2);
        EscapeRoom er5 = new EscapeRoom("Marti-Parti", Theme.HORROR, Difficulty.EASY, 30, 20, 2);
        EscapeRoom er6 = new EscapeRoom("Pop-Grigorii", Theme.FANTASY, Difficulty.EXTREME, 120, 40, 5);

        plat.addEscapeRoom(er1);
        plat.addEscapeRoom(er2);
        EscapeRoom[] escRooms = plat.getAllEscapeRooms();
        plat.addEscapeRoom(er3);
        plat.addEscapeRoom(er4);
        plat.addEscapeRoom(er4);
//        plat.addEscapeRoom(er5);
//        plat.addEscapeRoom(er6); //THROWS ERROR --> Correct
        //plat.addEscapeRoom(er5); //THROWS ERROR --> Correct

        plat.removeEscapeRoom("Popravka pri Borko");
        //plat.removeEscapeRoom("Popravka pri Borko"); //THROWS ERROR --> CORRECT
        plat.addEscapeRoom(er6);

//        plat.removeEscapeRoom("Pop-Grigorii");
//        plat.removeEscapeRoom("Stenata na pla4a");
//        plat.removeEscapeRoom("Piqn Vasilii");
//        plat.removeEscapeRoom("Lambdata na Trifon");
//        plat.removeEscapeRoom("Marti-Parti");

        plat.registerAchievement("Marti-Parti", "Yoanite", 23);
//        plat.registerAchievement("Marti-Parti", "Yoanite", 31); //THROWS ERROR --> CORRECT
//        plat.registerAchievement("Marti-Parti", "Yoanite", -1); //THROWS ERROR --> CORRECT
        plat.registerAchievement("Marti-Parti", "Yoanite", 22);
        plat.registerAchievement("Marti-Parti", "Yoanite", 15);

        plat.registerAchievement("Pop-Grigorii", "Burgaskite Batki", 60);
//        plat.registerAchievement("Popravka pri Borko", "Burgaskite Batki", 30);

//        Team topTeam = plat.getTopTeamByRating();

        plat.reviewEscapeRoom("Marti-Parti", new Review(5, "Lesno e no puk sega imam mnogo purhot po drehite"));
        plat.reviewEscapeRoom("Marti-Parti", new Review(7, "Lesno e"));
        plat.reviewEscapeRoom("Marti-Parti", new Review(5, "Lesno, i az imam purhot su6to taka che ne e 4ak takuv problem"));
        double martiPartiRating = er5.getRating();

        Review[] reviewsMartiParti = plat.getReviews("Marti-Parti");
        Review[] reviewsPopGrigorii = plat.getReviews("Pop-Grigorii");
    }
}