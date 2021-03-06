/*
 * Mast - Cast Web Media Player
 * Copyright (C) 2013 Bryan Emmanuel
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Bryan Emmanuel piusvelte@gmail.com
 */
package com.piusvelte.mast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends FragmentActivity implements
        OnClickListener {

    public static final String EXTRA_HOST = "com.piusvelte.webcaster.HOST";
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private EditText hostView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        hostView = (EditText) findViewById(R.id.setting_host);

        Intent intent = getIntent();

        if (intent != null) {
            hostView.setText(intent.getStringExtra(EXTRA_HOST));
        }

        Button submit = (Button) findViewById(R.id.button_submit);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_submit) {
            String host = hostView.getText().toString();
            if (TextUtils.isEmpty(host)) {
                Toast.makeText(this, R.string.error_host_empty, Toast.LENGTH_LONG).show();
            } else {
                setResult(
                        RESULT_OK,
                        new Intent().putExtra(EXTRA_HOST, hostView.getText().toString()));
                finish();
            }
        }
    }
}
