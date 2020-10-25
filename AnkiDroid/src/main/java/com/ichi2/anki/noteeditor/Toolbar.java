/*
 *  Copyright (c) 2020 David Allison <davidallisongithub@gmail.com>
 *
 *  This program is free software; you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software
 *  Foundation; either version 3 of the License, or (at your option) any later
 *  version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with
 *  this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ichi2.anki.noteeditor;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ichi2.anki.R;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class Toolbar extends FrameLayout {

    private TextFormatListener mFormatCallback;

    public Toolbar(@NonNull Context context) {
        super(context);
        init();
    }


    public Toolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public Toolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Toolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.note_editor_toolbar, this, true);
        setClick(R.id.note_editor_toolbar_button_bold, "<b>", "</b>");
        setClick(R.id.note_editor_toolbar_button_italic, "<em>", "</em>");
        setClick(R.id.note_editor_toolbar_button_underline, "<u>", "</u>");

        setClick(R.id.note_editor_toolbar_button_insert_mathjax, "\\(", "\\)");
        setClick(R.id.note_editor_toolbar_button_horizontal_rule, "", "<hr>");
        findViewById(R.id.note_editor_toolbar_button_font_size).setOnClickListener(l -> displayFontSizeDialog());
        findViewById(R.id.note_editor_toolbar_button_title).setOnClickListener(l -> displayInsertHeadingDialog());
    }

    public void setFormatListener(TextFormatListener formatter) {
        mFormatCallback = formatter;
    }

    private void displayFontSizeDialog() {
        String[] results = getResources().getStringArray(R.array.html_size_codes);

        // Might be better to add this as a fragment - let's see.
        new MaterialDialog.Builder(getContext())
                .items(R.array.html_size_code_labels)
                .itemsCallback((dialog, view, pos, string) -> {
                    String prefix = "<span style=\"font-size:" + results[pos] + "\">";
                    String suffix = "</span>";
                    TextWrapper formatter = new TextWrapper(prefix, suffix);
                    onFormat(formatter);
                })
                .title(R.string.menu_font_size)
                .show();
    }


    private void displayInsertHeadingDialog() {
        new MaterialDialog.Builder(getContext())
                .items(new String[] { "h1", "h2", "h3", "h4", "h5" })
                .itemsCallback((dialog, view, pos, string) -> {
                    String prefix = "<" + string + ">";
                    String suffix = "</" + string +">";
                    TextWrapper formatter = new TextWrapper(prefix, suffix);
                    onFormat(formatter);
                })
                .title(R.string.insert_heading)
                .show();
    }



    private void setClick(@IdRes int id, String prefix, String suffix) {
        setClick(id, new TextWrapper(prefix, suffix));
    }


    private void setClick(int id, TextFormatter textWrapper) {
        findViewById(id).setOnClickListener(l -> onFormat(textWrapper));
    }


    private void onFormat(TextFormatter formatter) {
        if (mFormatCallback == null) {
            return;
        }

        mFormatCallback.performFormat(formatter);
    }


    public interface TextFormatListener {
        void performFormat(TextFormatter formatter);
    }

    public interface TextFormatter {
        TextWrapper.StringFormat format(String s);
    }

    public static class TextWrapper implements TextFormatter {
        private final String mPrefix;
        private final String mSuffix;

        public TextWrapper(String prefix, String suffix) {
            this.mPrefix = prefix;
            this.mSuffix = suffix;
        }


        @Override
        public StringFormat format(String s) {
            StringFormat stringFormat = new StringFormat();
            stringFormat.result = mPrefix + s + mSuffix;
            if (s.length() == 0) {
                stringFormat.start = mPrefix.length();
                stringFormat.end = mPrefix.length();
            } else {
                stringFormat.start = 0;
                stringFormat.end = stringFormat.result.length();
            }

            return stringFormat;
        }

        public static class StringFormat {
            public String result;
            public int start;
            public int end;
        }
    }

}
