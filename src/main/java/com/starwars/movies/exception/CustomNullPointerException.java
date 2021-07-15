package com.starwars.movies.exception;

public class CustomNullPointerException extends NullPointerException
{
    public CustomNullPointerException()
    {
        super();
    }
    
    public CustomNullPointerException(String s)
    {
        super(s);
    }
}
