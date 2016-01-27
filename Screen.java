import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * This is an adaptation from the screen class of Actor subclass
 * from Dr. BA Canada's Plants Vs. Zombies greenfoot project
 * 
 * @Author SassyPantz 
 * @Version 2014.12.14
 */
public class Screen extends Actor
{
    public Screen( GreenfootImage screenImage )
    {
        setScreen( screenImage );
        getImage().setTransparency( 0 );
    } // end screen constructor
    
    /**
     * 
     */
    public void setAlpha( int alpha ) 
    {        
        getImage().setTransparency( alpha );     
    } // end method setAlpha 
    
    public void setScreen( GreenfootImage screenImage )
    {        
        setImage( screenImage );        
    } // end method setScreen
    
} // end class Screen