public class TourGuide
{
    public static int getBestSightseeingPairScore(int[] places)
    {
        int highestRating = 0;
        boolean initHighestRating = false;

        for (int i = 0; i < places.length - 1; i++) {
            for (int j = i + 1; j < places.length; j++) {
                int place1Rating = places[i];
                int place2Rating = places[j];

                int grade = place1Rating + place2Rating + i - j;

                if(grade > highestRating && initHighestRating) highestRating = grade;
                else if(initHighestRating == false) {
                    highestRating = grade;
                    initHighestRating = true;
                }
            }
        }

        return highestRating;
    }
}
