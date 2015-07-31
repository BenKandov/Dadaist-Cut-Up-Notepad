
package come.up.BuhBuhBen;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;

public class CutUp extends Activity {

    private EditText text;
    private EditText title;

    private Long mRowId;
    private NotesDbAdapter mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);
        text = (EditText)findViewById(R.id.input);
        title = (EditText)findViewById(R.id.title);
        Button save = (Button)findViewById(R.id.confirm);

        mRowId = (savedInstanceState ==null)?null:
                (Long)savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if(mRowId==null){
            Bundle extras = getIntent().getExtras();
            mRowId = extras !=null?extras.getLong(NotesDbAdapter.KEY_ROWID)
                    :null;
        }


        populateFields();



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });


    }
    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            title.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            text.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);

    }

    @Override
    protected void onPause(){
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume(){
        super.onResume();

        populateFields();

    }

    private void saveState(){
       String t;

        if(title.getText().toString().equals("")){
           t = " \"untitled\"  ";
        }else{
           t= title.getText().toString();
        }

        String content = text.getText().toString();


        if(mRowId == null) {
            long id = mDbHelper.createNote(t, content);
            if (id > 0) {
                mRowId = id;
            }
        }else{
                mDbHelper.updateNote(mRowId,t,content);
            }

        }



    public boolean cutUpText(View view){

        ArrayList<String> textComp = new ArrayList<String>();
        String middleMan = text.getText().toString();
        int size = middleMan.length();
        boolean portis = false;
        int place = 0;


        if(!middleMan.contains(" ")){
            return false;
        }
        String addOn = "";

        while(place<size){
            addOn = "";
            while((place<size)&&(middleMan.charAt(place)!=' ')){
                addOn+=middleMan.charAt(place);
                place++;
                portis = true;
            }

            if(portis){
                textComp.add(addOn);

                portis=false;
                place--;
            }
            if(place>=size){
                break;
            }
            place++;
        }



        Collections.shuffle(textComp);
        String output ="";
        size = textComp.size();
        for(int i = 0;i<size;i++){
            output+=textComp.get(i);
            output+=" ";
        }
        text.setText(output);

        saveState();
        return true;

    }

}
