package com.gulaxoft.callmelater;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentHome extends Fragment {

    private ImageButton turnOnOffButton;
    Toast toastOn, toastOff;

    public void setCaptionText (String text) {
        ((TextView) getActivity().findViewById(R.id.captionText))
                .setText(text);
    }

    public void setResultText (String text) {
        ((TextView) getActivity().findViewById(R.id.resultText))
                .setText(text);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_layout, container, false);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ((TextView)getActivity().findViewById(R.id.resultText))
                .setText(Html.fromHtml("say <b>'Call me later'</b> to reject<br/>an incoming call"));
        setTurnOnOffButton();
        toastOn = Toast.makeText(getActivity(),
                "Recognition activated", Toast.LENGTH_SHORT);
        toastOff = Toast.makeText(getActivity(),
                "Recognition deactivated", Toast.LENGTH_SHORT);
    }

    public void setTurnOnOffButton() {
        turnOnOffButton = (ImageButton)getActivity().findViewById(R.id.turnBtn);
        turnOnOffButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!Settings.isRecognitionEnabled()) {
                    turnOnOffButton.setImageResource(R.drawable.ic_recognition_enabled);
                    Settings.enableRecognition();
                    toastOn.show();
                } else {
                    turnOnOffButton.setImageResource(R.drawable.ic_recognition_disabled);
                    Settings.disableRecognition();
                    toastOff.show();
                }
            }
        });
    }
}
