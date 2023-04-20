package com.example.timeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //Importing all the widget classes.
    Button start, pause, stop, enter;
    TextView time;
    Timer timer;
    TimerTask timerTask;
    EditText setWorkoutTime, setRestTime;
    ProgressBar progressBar;

    //setting some variables to store times such as times at pause, time when stop is clicked, initial workout time so that
    //these variables can be used across different setOnClickListener methods.
    int workoutTime;
    int restTime;
    int initialWorkoutTime; // variable to store the initial workout time

    int pauseWorkoutTime;
    int initialRestTime;
    int stoppedWorkoutTime;

    //Counts the number of times pause and stop have been clicked.
    int pauseClicked=0;
    int stopClicked=0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find view by id for all the widgets used.
        start = findViewById(R.id.start);
        pause = findViewById(R.id.pause);
        stop = findViewById(R.id.stop);
        enter = findViewById(R.id.enter);
        time = findViewById(R.id.time);
        setWorkoutTime = findViewById(R.id.setWorkoutTime);
        setRestTime = findViewById(R.id.setRestTime);
        progressBar = findViewById(R.id.progressBar);
        time.setText(String.valueOf(pauseWorkoutTime));

        //When the enter button is clicked, the workout time entered in the edit text is shown in the text view (timer).
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeWorkout = setWorkoutTime.getText().toString();
                workoutTime = Integer.parseInt(timeWorkout);
                initialWorkoutTime = workoutTime; // store the initial workout time
                time.setText(String.valueOf(workoutTime));
            }
        });


        /** Alright, this start setOnClickListener has lots of logical functionality. The way that this works is that whenever I click
         * the start button, the countdown decrements. I use timer task for this, run on ui thread and runnable here.
         * From the stop setOnClickListener, I used the stoppedWorkoutTime variable which I would take from the workoutTime so that I could
         * passover stoppedWorkoutTime into start setOnClickListener while not affecting workoutTime. I did a similar thing with pauseWorkoutTime
         * where it proved to be more useful. With pauseWorkoutTime I had to do some if statements where I would have to perform actions
         * based on how many times the pause button had been clicked and how many times the stop button had been clicked. **/
        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //startClicked++;
                //pauseClicked=0;
                System.out.println(pauseClicked);
                timer = new Timer();
                workoutTime = initialWorkoutTime;

                //progressBar.setProgress(workoutTime);

                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            public void run() {
                                time.setText(String.valueOf(workoutTime));
                                String timeRest = setRestTime.getText().toString();
                                restTime = Integer.parseInt(timeRest);
                                initialRestTime = restTime;
                                if (workoutTime == initialWorkoutTime) {
                                    timerTask = new TimerTask() {
                                        @Override
                                        public void run() {

                                            //progressBar.incrementProgressBy(1);
                                            if (pauseClicked>=1)
                                            {
                                                workoutTime = pauseWorkoutTime;
                                                pauseWorkoutTime--;
                                                //Increments progress by percentage: the workoutTime I had before pausing and the pauseWorkoutTime
                                                //value which is being decremented.
                                                progressBar.incrementProgressBy(workoutTime/pauseWorkoutTime);


                                                if (pauseWorkoutTime==0) {
                                                    pauseWorkoutTime = initialWorkoutTime;
                                                    //This is so that the progress bar fills up at the right portion for each second.
                                                    progressBar.setProgress(100/initialWorkoutTime);
                                                }



                                            }

                                            if (stopClicked>=1) {
                                                pauseClicked=0;
                                                workoutTime = stoppedWorkoutTime;
                                                stoppedWorkoutTime--;

                                                if (stoppedWorkoutTime==0) {
                                                    stoppedWorkoutTime = initialWorkoutTime;
                                                }

                                            }



                                            runOnUiThread(new Runnable() {

                                                public void run() {

                                                    time.setText(String.valueOf(workoutTime));
                                                    workoutTime--;
                                                    progressBar.incrementProgressBy(100/initialWorkoutTime);

                                                }

                                            });
                                        }

                                    };
                                    timer.scheduleAtFixedRate(timerTask, 0, 1000);
                                }


                                if (workoutTime == 0) {
                                    timer.cancel();
                                    progressBar.setProgress(0);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            time.setText(String.valueOf(initialWorkoutTime));

                                            if (workoutTime < 0) {
                                                timer.cancel();
                                                workoutTime = initialWorkoutTime; // reset workout time to its initial value
                                                //initialWorkoutTime--;
                                                time.setText(workoutTime);
                                                progressBar.setProgress(100/initialWorkoutTime);

                                            }



                                        }
                                    }, (restTime * 1000));

                                }


                                    //System.out.println(initialWorkoutTime);

                            }
                        });
                    }
                };
                timer.scheduleAtFixedRate(timerTask, 0, 1000);
            }
        });

        // When pause is clicked, I increment pauseClicked, stop the timer and store
        // the workoutTime at that moment in pauseWorkoutTime so that I could pass it over into start onClickListener.
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseClicked++;
                timer.cancel();
                pauseWorkoutTime = workoutTime;
            }
        });

        //What happens when the reset button is clicked: the timer is cancelled and the initial workout time is passed over to stoppedWorkoutTime,
        //a variable that I can passover into the start setOnClickListener. I increment the number of times stopClicked has been clicked.
        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            timer.cancel();
            stoppedWorkoutTime = initialWorkoutTime;
            time.setText(String.valueOf(stoppedWorkoutTime));
            progressBar.setProgress(0);
            stopClicked++;


            }
        });
    }
}
