import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * This is an adaptation from Neil Brown's Typing greenfoot Scenario
 * 
 * With assistance from Dr. BA Canada on multiple facets
 * and from Andrew Wetmore on using point to access arrays of arrays
 * 
 * @Author SassyPantz 
 * @Version 2014.12.14
 */
public class Typing extends World 
{
    private long time;
    private static final int TEXT_SIZE=10; 
    private static final int LINE_HEIGHT=20;
    private static final int START_X=40;
    private static final int START_Y=40;
    private static final int X_SIZE=600;
    private static final int Y_SIZE=400;

    private static final int returnChar = 0x23CE; // Unicode symbol for return character
    private static final int spaceChar = 0x203F;  // Unicode symbol for undertie character

    private static final String returnString = Character.toString( (char) returnChar );
    private static final String spaceString = Character.toString( (char) spaceChar );

    private String code[] = {"import java.awt.Color;" + returnString + "\n",//,{returnString + "\n"};//
                             "import java.awt.Font;" + returnString + "\n",
                             "import java.awt.Point;" + returnString + "\n", 
                             "import greenfoot.*;" + returnString + "\n", returnString + "\n", 
                             "public class Typing extends World" + returnString + "\n",
                             "{" + returnString + "\n",
                             "private long time;" + returnString + "\n",
                             "private static final int TEXT_SIZE=10;"  + returnString + "\n",
                             "private static final int LINE_HEIGHT=20;" + returnString + "\n",
                             "private static final int START_X=40;" + returnString + "\n",
                             "private static final int START_Y=40;" + returnString + "\n",
                             "private static final int X_SIZE=600;" + returnString + "\n",
                             "private static final int Y_SIZE=400;"  + returnString + "\n",};
                          
    private Point location = new Point(0,0);

    private char curChar;

    // the world "has" gameStarted
    private boolean gameStarted = true; 

    // typing started
    private boolean typingStarted = false;

    // the world "has" background music
    private GreenfootSound bgMusic;
    // the world "has" a sound to play when user is typing
    private GreenfootSound typingSound;
    // the world "has" a sound to play when user hits the 'enter key'
    private GreenfootSound returnCarriageSound;
    // the world "has" a sound to play when user is Incorrectly typing
    private GreenfootSound typingErrorSound;
    // the world "has" a sound to play when user hits the 'backspace key'
    private GreenfootSound backspaceSound;
    // the world "has" a sound to play when user hits the 'shift key'
    private GreenfootSound shiftSound;
    // the world "has" a sound to play when user hits the 'space key'
    private GreenfootSound spaceSound;
    // end of game sound
    private GreenfootSound theEndSound;
    // empty line sound: yeahBabySound
    private GreenfootSound yeahBabySound;

    private int stageCycleCount = 0;
    private int theScore = 0;

    public int errorCount = 0;
    private Text errorCountText;

    private int keystrokeCount = 0;
    private Text keystrokeCountText;

    private GreenfootImage titleScreen = new GreenfootImage( "SharKEYStitleScreen.png" );
    private Screen screen;
    private int alpha = 0;

    private long whatTime = 0;
    private Text whatTimeText;

    /*
     *    
     */ 
    public Typing()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        // (the world is unbounded, so we supply a 4th argument, false, to the superclass constructor)
        super( X_SIZE, Y_SIZE, 1, false );

        time = System.currentTimeMillis();

        // set font to Monospaced, bold and size 16
        getBackground().setFont( new Font( Font.MONOSPACED, Font.BOLD, 16 ) );
        // make the text black, to start
        getBackground().setColor( Color.BLACK );

        // create the GreenfootSound object to be used as background music
        bgMusic = new GreenfootSound( "In_Fast_Motion.mp3" );

        // create the GreeenfootSound object to be used when user is typing
        typingSound = new GreenfootSound( "SmackPunch.mp3" );

        // create the GreeenfootSound object to be used when user hits enter key
        returnCarriageSound = new GreenfootSound( "Dot_Matrix_Printer.mp3" );

        // create the GreeenfootSound object to be used when user is backspacing
        backspaceSound = new GreenfootSound( "RecordScratchDamage.mp3" );

        // create the GreeenfootSound object to be used when user is Incorrectly typing
        typingErrorSound = new GreenfootSound( "pluck.mp3" );

        // create the GreeenfootSound object to be used when user is shifting
        shiftSound = new GreenfootSound( "SuperMarioBrothers-1upExtraLife.mp3" );

        // create the GreeenfootSound object to be used when user hits spacebar
        spaceSound = new GreenfootSound( "BalloonPop.mp3" ); // was Fart01.mp3

        // create the GreeenfootSound object to be used when game is over
        theEndSound = new GreenfootSound( "ThatsAllFolks.mp3" );

        // create the GreeenfootSound object to be used when the line to be typed only contains a return carriage
        yeahBabySound = new GreenfootSound( "YeahBaby.mp3" );

        for( int line=0;line<code.length;line++ )
        {
            // draw every character to be typed on screen
            getBackground().drawString( code[line], START_X, START_Y+LINE_HEIGHT*line );
        } // end for loop

        // set up title screen
        screen = new Screen( titleScreen );
        addObject( screen, X_SIZE/2, Y_SIZE/2 );
        screen.setAlpha( 255 );      
    } // end constructor Typing

    public void act()
    {        
        if ( gameStarted )
        {
            gameStarted = false;

            // this is displaying the string you are supposed to type
            getBackground().setColor( Color.black );

            // setUp whatTime count text field
            whatTimeText = new Text( 30 ); // size of text and counter in window
            addObject( whatTimeText, X_SIZE - 150, Y_SIZE - 60 ); // location of text & whatTime in window
            whatTimeText.setText( "Time in Seconds: " + "" + 0 ); // what text is displayed before the counter

            // set up keystroke count text field
            keystrokeCountText = new Text( 30 );  // size of text and counter in window            
            addObject( keystrokeCountText, X_SIZE - 150, Y_SIZE - 40 ); // location of text & keystrokCount in window
            keystrokeCountText.setText( "Keys entered: " + "" + 0 ); // what text is displayed before the counter

            // set up keystroke count text field
            errorCountText = new Text( 30 );  // size of text and counter in window               
            addObject( errorCountText, X_SIZE - 150, Y_SIZE - 20 ); // location of text & errorCount in window
            errorCountText.setText( "Errors entered: " + "" + 0 ); // what text is displayed before the counter
            
            if( errorCount >= 1)
            {
                errorCountText.getImage().setColor( Color.RED );
            }
        } // end if

        if( typingStarted )
        {
            // set whatTime as the current computer time in milliseconds minus time elapsed all over 1000
            whatTime = (System.currentTimeMillis()-time)/1000; 
            // what to display for whatTime text: text string, followed by whatTime
            whatTimeText.setText( "Time in Seconds: " + "" + whatTime );             
        }

        stageCycleCount++; // post increment stageCycle Count

        if( stageCycleCount >= 1 && stageCycleCount <= 25)
        {
            displayTitleScreen();
        }

        // currently entered "key" from the user is stored as a string named keystroke
        String keystroke = Greenfoot.getKey(); 

        if( keystroke == "shift" ) // if key entered is the shift key
        {
            // play shiftSound (but first, stop shiftSound if it's currently playing)
            shiftSound.stop();
            shiftSound.play();            
        }

        if( keystroke != null && keystroke != "shift" ) // if user is currently typing and also not 'shifting'
        {
            // current character to type is the code at location to be typed
            curChar = code[( int ) location.getY()].charAt(( int ) location.getX());            
            if( errorCount >= 1) // if error count is equal to or more than 1
            {
                errorCountText.getImage().setColor( Color.RED ); // make errorCountText display red
            }
            
            if( !typingStarted )
            {
                typingStarted = true;  
                // this computes the elapsed time since you started the game
                time = System.currentTimeMillis();
            }

            ++keystrokeCount; //preincrement keystroke count
            // what to display for keystroke count text: text string, followed by keystrokeCount
            keystrokeCountText.setText( "Keys entered: " + "" + keystrokeCount );

            if( keystroke.equals( "backspace" ) ) // if the keystroke entered by user is the backspace
            {
                System.out.println( "You Typed: '" + keystroke + "'\t" + "Target key: '" + curChar + "'" );

                // play typingSound (but first, stop typingSound if it's currently playing)
                backspaceSound.stop();
                backspaceSound.play();

                if( location.getX() != 0) // if not at the beginning of the line (left side of screen) 
                {
                    location.translate(-1, 0); // go back in "location" one character space to the left
                    // current character to type is the code at location to be typed
                    curChar = code[( int ) location.getY()].charAt(( int ) location.getX());
                    // make curChar black (again if it needs to be rewritten/ has been backspaced over)
                    getBackground().setColor( Color.BLACK ); 
                    if( curChar == '\n') // if current character to type is new line character
                    {
                        drawAtLocation( returnString ); // draw returnString on display at that location
                    }
                    else if( curChar == ' ') // if current character to type is a space
                    {
                        drawAtLocation( spaceString ); // draw spaceString on display at that location
                    }
                    else // otherwise:
                    {
                        // draw the a string of the current character to be typed
                        drawAtLocation( String.valueOf( curChar ) ); 
                    }                  
                } // end if

            } // end if keystroke equals backspace
            else
            {
                // current character to type is the code at location to be typed
                curChar = code[( int ) location.getY()].charAt(( int ) location.getX());

                if( curChar == returnChar )
                {                    
                    if( location.getX() == 0) // if not at the beginning of the line (left side of screen) 
                    {
                        // play yeahBabySound (but first, stop returnCarriageSound if it's currently playing)
                        returnCarriageSound.stop();
                        yeahBabySound.stop();
                        yeahBabySound.play();
                    }
                    else
                    {
                        // play returnCarriageSound (but first, stop typingSound if it's currently playing)
                        typingSound.stop();
                        returnCarriageSound.stop();
                        returnCarriageSound.play();
                    }
                    location.translate(1, 0); // move location at x-axis one (per correct keystroke entered)
                    // current character to type is the code at location to be typed
                    curChar = code[( int ) location.getY()].charAt(( int ) location.getX());
                }                
                // if keystroke is "space" make a space, (i.e., " ") otherwise do the keystroke entered
                keystroke = keystroke.equals( "space" ) ? " " : keystroke;

                System.out.println( "You Typed: '" + keystroke + "'\t" + "Target key: '" + curChar + "'" );

                // if keystroke is the same as the desired code to type and NOT a space
                if( !keystroke.equals(" ") && keystroke.equals( String.valueOf( curChar )) )
                {
                    // play typingSound (but first, stop typingSound if it's currently playing)
                    typingSound.stop();
                    typingSound.play();

                    // draw the String, current character entered, in light gray
                    getBackground().setColor( Color.LIGHT_GRAY );
                    drawAtLocation( String.valueOf( curChar ) );

                    double speed = ( double )( 100 )/whatTime;
                    
                    UserInfo me = UserInfo.getMyInfo();
                    if( !keystroke.equals( "shift" ) ) // if keystroke is NOT shift
                    {
                        location.translate(1,0); // move location at x-axis one
                    } // end if
                    else // otherwise, if shift is the keystroke
                    {
                        location.translate(0,0); // do NOT move location at x-axis one
                    } // end else
                    
                    if ( me != null )
                    {
                        if ( speed > me.getScore())
                        {
                            me.setScore( ( int )Math.round( speed ) );
                            me.store();
                        } // end if                        
                    } // end if                    

                    else
                    {
                        getBackground().drawString( ""+whatTime,550,350 );
                    } // end else

                } // end if

                /*
                 * if there IS a spaceString at this location, and I type a space
                 * then paint it white
                 */
                else if( keystroke.equals( " " ) && curChar==' ' )
                {
                    //System.out.println( "I should be typing a spaceString right now" );
                  
                    // play spaceSound (but first, stop spaceSound if it's currently playing)
                    spaceSound.stop();
                    spaceSound.play();

                    // set the color to draw as white
                    getBackground().setColor( Color.WHITE );
                    
                    // draw a spacestring (white) at that location                    
                    drawAtLocation(spaceString);
                    location.translate( 1, 0 ); // move location once on x-axis
                } // end else if

                else if( !keystroke.equals( "space" ) && curChar==' ' )
                {
                    // set the color to draw as red
                    getBackground().setColor( Color.RED );

                    drawAtLocation(spaceString); // draw a spacestring (red) at that location
                    location.translate( 1, 0 ); // move location once along x-axis

                    // play typingErrorSound (but first, stop typingErrorSound if it's currently playing)
                    typingErrorSound.stop();
                    typingErrorSound.play();

                    ++errorCount; // preincrement errorCount
                    errorCountText.getImage().setColor( Color.RED ); // set color of text to red
                    errorCountText.setText( "Errors entered: " + "" + errorCount ); // display errors text & counter
                } // end else if

                // else if the keystroke is "enter" and the current code to type is new line character then:
                else if( keystroke.equals( "enter" ) && curChar=='\n' )
                {                    
                    getBackground().setColor( Color.WHITE ); // set the color to draw as white
                    
                    location.translate( -1, 0 ); // move location back once along x-axis
                    drawAtLocation(returnString); // draw returnString (in white) at the location
                    location.translate( 1, 0 ); // move location once along x-axis
                    
                    // at one 'character space' beyond the line of code, end of the line
                    location.translate( -code[( int ) location.getY()].length()+1,  1 );
                } // end else if

                else if( curChar=='\n' && !keystroke.equals( "enter"))
                {
                    getBackground().setColor( Color.RED) ; // set the color to draw as red
                    location.translate( -1, 0 );  // move location back once along x-axis
                    drawAtLocation(returnString); // draw returnString (in red) at the location
                    location.translate(1,0); // move location once along x-axis

                    // play typingErrorSound (but first, stop typingErrorSound if it's currently playing)
                    typingErrorSound.stop();
                    typingErrorSound.play();

                    ++errorCount; // preincrement errorCount
                    errorCountText.getImage().setColor( Color.RED ); // set color of text to red
                    errorCountText.setText( "Errors entered: " + "" + errorCount ); // display errors text & counter                    
                }
                
                else
                {
                    // draw the String, current character entered *INCORRECTLY*, in red
                    getBackground().setColor( Color.RED );
                    drawAtLocation( String.valueOf( curChar ) );
                    // move 'location' one to the right once, per character entered
                    location.translate( 1, 0 ); 

                    // play typingErrorSound (but first, stop typingErrorSound if it's currently playing)
                    typingErrorSound.stop();
                    typingErrorSound.play();

                    ++errorCount; // preincrement errorCount
                    errorCountText.getImage().setColor( Color.RED ); // set color of text to red
                    errorCountText.setText( "Errors entered: " + "" + errorCount ); // display errors text & counter
                } // end else

            } // end else

        } // end if

        /*
         * If the 'cursor' is at the end of the string to be typed
         * AND the LAST character typed was the LAST character you're supposed to type...
         */
        if( code.length == ( int )location.getY()  )
        {
            // set speed as keystrokeCount minus errorCount all over whatTime
            double speed = ( double )( keystrokeCount = keystrokeCount - errorCount )/whatTime;             

            UserInfo me = UserInfo.getMyInfo();
            if( me != null )
            {
                // set my score as an int, from the rounded speed
                me.setScore( ( int )Math.round( speed ) );
                me.store(); 
            } // end if
             // add object new scoreboard, sized 600x400 px. In middle of screen
            addObject( new ScoreBoard( 600,400),300,200 );

            // play theEndSound (but first, stop returnCarriageSound if it's currently playing)
            returnCarriageSound.stop();
            theEndSound.stop();
            theEndSound.play();
            
            if( !yeahBabySound.isPlaying() )
            {
                yeahBabySound.stop();
                theEndSound.play();                
            }
            
            Greenfoot.stop(); // stop Greenfoot
        } // end if
       
    } // end method act

    private void drawAtLocation( String text )
    {  
        getBackground().drawString( text, ( int )( START_X+location.getX()*TEXT_SIZE ), 
            ( int )( START_Y+LINE_HEIGHT*location.getY()));
    } // end method drawAtLocation
      
    public void displayTitleScreen()
    {
        if ( stageCycleCount == 1 )
        {
            alpha = 255;
            screen.setScreen( titleScreen );
            screen.setAlpha( alpha );
        } // end if
        else if ( stageCycleCount > 1 && stageCycleCount < 25 )
        {
            alpha -= 10; // alpha is reassigned as alpha minus 10
            screen.setAlpha( alpha );
        } // end else if
        else if ( stageCycleCount >= 25 )
        {
            stageCycleCount = 0;
            removeObject( screen );
        } // end else if      
    } // end method displayTitleScreen
    
    /**
     * Defines all actions taken when we want to get errorCount
     */     
    public int getErrorCount()
    {        
        return errorCount;        
    } // end method stopped
        
    /**
     *  Defines all actions taken when we hit the pause button
     */
    public void stopped()
    {
        if ( bgMusic.isPlaying() ) // if bgMusic is playing
        {
            bgMusic.pause(); // pause bgMusic
        } // end if
    } // end method stopped

    /**
     * Defines all actions taken when the run button is hit
     */
    public void started()
    {
        if ( !bgMusic.isPlaying() ) // if bgMusic is not playing
        {
            bgMusic.playLoop(); // play bgMusic on loop
        } // end if
    } // end method started

} // end class Typing
