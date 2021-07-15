package com.starwars.movies.utility;

public class ConvertToInchesAndFeet
{
    private static final double CM_INCHES = 2.54;
    private static final double CM_FEET = 30.48;
    
    public static double getMovieCharTotalHeightForGenderInches(int cm)
    {
        return cm / CM_INCHES;
    }
    
    public static double getMovieCharTotalHeightForGenderFeet(int cm)
    {
        return cm / CM_FEET;
    }
}
