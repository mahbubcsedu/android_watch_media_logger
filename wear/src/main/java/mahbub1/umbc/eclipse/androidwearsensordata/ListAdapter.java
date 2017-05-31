package mahbub1.umbc.eclipse.androidwearsensordata;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by mahbub on 4/12/17.
 */

public class ListAdapter extends AbstractListAdapter<ListAdapter.Entity, ListAdapter.ViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;

    public ListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(
                mInflater.inflate(R.layout.section_item, viewGroup, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.bind(mData.get(position));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public static class Entity {
        private final String mTitle;

        public Entity(String title) {
            mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Entity entity = (Entity) o;

            if (mTitle != null ? !mTitle.equals(entity.mTitle) : entity.mTitle != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return mTitle != null ? mTitle.hashCode() : 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTextView;
        private Entity mEntity;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.label);
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mEntity);
                    }
                }
            });
        }

        public void bind(Entity entity) {
            mEntity = entity;
            mTextView.setText(entity.getTitle());
        }

        public TextView getTextView() {
            return mTextView;
        }

        @Override
        public String toString() {
            return "ViewHolder{" + mTextView.getText() + "}";
        }
    }

    public static interface OnItemClickListener {
        public void onItemClick(Entity entity);
    }
}