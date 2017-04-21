package mahbub1.umbc.eclipse.androidwearsensordata.events;

import mahbub1.umbc.eclipse.androidwearsensordata.data.TagData;

/**
 * Created by mahbub on 2/1/17.
 */

public class TagAddEvent {
    private TagData mTagData;

    public TagAddEvent(TagData pTagData) {
        mTagData = pTagData;
    }

    public TagData getTag() {
        return mTagData;
    }
}
