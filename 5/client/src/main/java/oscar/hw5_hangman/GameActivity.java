package oscar.hw5_hangman;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import common.ClientToServer;
import common.ServerToClient;


public class GameActivity extends AppCompatActivity implements UpdateResponse {
    private String enteredGuess;
    private Button guessBtn;
    private EditText guessField;
    private TextView status;
    private TextView totalWins;
    private TextView guessesLeft;
    private ServerConnection server;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private boolean keyboardVisible = false;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initAlertDialog();

        connectToServer();

        initInputOperators();

    }

    private void initAlertDialog() {
        this.builder = new AlertDialog.Builder(GameActivity.this);
        this.builder.setTitle(getString(R.string.dialog_connect_title)).setMessage(R.string.connecting);
        this.dialog = builder.create();
        this.dialog.setCanceledOnTouchOutside(true);
    }

    private void connectToServer() {
        new ConnectServer().execute();
    }

    private void initInputOperators() {
        guessBtn = (Button) findViewById(R.id.guessButton);
        guessField = (EditText) findViewById(R.id.guessField);
        status = (TextView) findViewById(R.id.status);
        totalWins = (TextView) findViewById(R.id.totalwins);
        guessesLeft = (TextView) findViewById(R.id.triesleft);
        imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);

        guessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredGuess = guessField.getText().toString();
                guessField.getText().clear();

                if(server.isConnected()) {
                    new SendServer().execute(enteredGuess);
                } else {
                    lostOrNoConnection(getString(R.string.lost_connection));
                }
            }
        });

        guessField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guessField.getText().clear();
                keyboardVisible = true;
            }
        });

        guessField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_ENTER ) {
                    guessBtn.performClick();
                }
                return false;
            }
        });
    }

    private void lostOrNoConnection(String dialogMsg) {
        dialog.setMessage(dialogMsg);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                GameActivity.this.finish();
            }
        });
        if(!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void showKeyboard() {
        if(!keyboardVisible && getCurrentFocus() != null) {
            imm.showSoftInput(getCurrentFocus(), 0);
            keyboardVisible = true;
        }
    }

    private void hideKeyboard() {
        if(keyboardVisible && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            keyboardVisible = false;
        }
    }

    @Override
    public void updateServerResponse(final Object responseObj) {
        ServerToClient response = (ServerToClient) responseObj;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText(response.getStatus());
                totalWins.setText(String.format("%s", response.getTotalWins()));
                guessesLeft.setText(String.format("%s", response.getTriesLeft()));
                if(!response.isGameRunning()) {
                    guessBtn.setText(getString(R.string.new_game_btn));
                    hideKeyboard();
                } else {
                    guessBtn.setText(getString(R.string.guess_btn));
                    showKeyboard();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            server.disconnect();
        } catch(Exception ex) {}
    }


    private class ConnectServer extends AsyncTask<Void, Void, ServerConnection> {

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected ServerConnection doInBackground(Void... voids) {
            ServerConnection serverConnection = new ServerConnection(GameActivity.this);
            try {
                serverConnection.connect();
            } catch (Exception ex) {
                return null;
            }

            return serverConnection;
        }

        @Override
        protected void onPostExecute(ServerConnection serverConnection) {
            if(serverConnection != null) {
                GameActivity.this.server = serverConnection;
                new Thread(serverConnection).start();
                dialog.setMessage(getString(R.string.yes_gameserver));
                new SendServer().execute("");
            } else {
                lostOrNoConnection(getString(R.string.no_gameserver));
            }
        }
    }

    private class SendServer extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            GameActivity.this.server.sendGuess((new ClientToServer(strings[0])));
            return null;
        }
    }

}
