package de.uni_stuttgart.informatik.sopra.sopraapp.feature.sidebar.profile;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException;

public class ProfileImageGridViewAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    /**
     * Interface which returns the drawable and the polygonType which was selected.
     */
    interface OnImageSelected {
        void onImageClicked(Drawable drawable, int position) throws NoUserException;
    }

    private OnImageSelected onImageSelected;
    private List<ImageView> imageList;

    ProfileImageGridViewAdapter(List<ImageView> imageList) {
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return imageList.get(position);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onImageSelected != null) {

            try {
                onImageSelected.onImageClicked(imageList.get(position).getDrawable(), position);
            } catch (NoUserException e) {
                e.printStackTrace();
            }


        }

    }

    /**
     * Will be invoked on image selection.
     *
     * @param onImageSelected the interface to invoke
     */
    public void setOnImageSelected(OnImageSelected onImageSelected) {
        this.onImageSelected = onImageSelected;
    }
}
