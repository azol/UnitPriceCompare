package leoliang.android.widget.util;

import java.util.HashSet;
import java.util.Set;

import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * Compound multiple <code>RadioGroup</code>s into one group. Check one <code>RadioButton</code> will cause all other
 * groups clear check.
 * 
 * Must not call <code>setOnCheckedChangeListener()</code> on a <code>RadioGroup</code> which is added to a
 * <code>CompoundRadioGroup</code>.
 */
public class CompoundRadioGroup {

    private Set<RadioGroup> groups = new HashSet<RadioGroup>();
    private boolean switchingGroup = false;
    private boolean disableCheckedChangeListener;
    private OnCheckedChangeListener onCheckedChangeListener = null;
    private OnCheckedChangeListener groupSwitcher = new OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // switchingGroup: Workaround for Android issue 4785, http://code.google.com/p/android/issues/detail?id=4785
            if ((checkedId == -1) || switchingGroup || disableCheckedChangeListener) {
                return;
            }
            switchingGroup = true;
            clearGroupsOtherThan(group);
            switchingGroup = false;

            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(group, checkedId);
            }
        }
    };

    private void clearGroupsOtherThan(RadioGroup group) {
        for (RadioGroup g : groups) {
            if (g != group) {
                g.clearCheck();
            }
        }
    }

    public void add(RadioGroup group) {
        groups.add(group);
        group.setOnCheckedChangeListener(groupSwitcher);
    }

    public int getCheckedRadioButtonId() {
        int id = -1;
        for (RadioGroup g : groups) {
            id = g.getCheckedRadioButtonId();
            if (id != -1) {
                break;
            }
        }
        return id;
    }

    /**
     * Sets the selection to the radio button whose identifier is passed in parameter.
     * 
     * Note: <code>OnCheckedChangeListener</code> will not be invoked.
     * 
     * @param id
     */
    public void check(int id) {
        disableCheckedChangeListener = true;
        for (RadioGroup g : groups) {
            g.check(id);
        }
        disableCheckedChangeListener = false;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }
}
