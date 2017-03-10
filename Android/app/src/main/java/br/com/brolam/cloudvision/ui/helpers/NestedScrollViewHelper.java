/*
 * Copyright (C) 2017 Breno Marques
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.brolam.cloudvision.ui.helpers;

import android.content.Context;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;

/**
 * Somene disponibilizar os métodos onSaveInstanceState e onRestoreInstanceState
 * que são definidos como protected na super class. {@link ActivityHelper}
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class NestedScrollViewHelper extends CoordinatorLayout {

    public NestedScrollViewHelper(Context context) {
        super(context);
    }

    public NestedScrollViewHelper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedScrollViewHelper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Parcelable getRestoreInstanceState() {
        return super.onSaveInstanceState();
    }


    public void restoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }
}
