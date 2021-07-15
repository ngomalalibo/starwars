package com.starwars.movies.exception;

public class EntityNotFoundException extends RuntimeException
{
    
    public EntityNotFoundException(String message)
    {
        super(message);
    }
}
