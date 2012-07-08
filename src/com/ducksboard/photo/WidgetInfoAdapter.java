package com.ducksboard.photo;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class WidgetInfoAdapter extends ArrayAdapter<WidgetInfo> {

    public WidgetInfoAdapter(Context context, int textViewResourceId,
            List<WidgetInfo> widgets) {
        super(context, textViewResourceId, widgets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.widget_item, null);
        }

        TextView widgetTitleText = (TextView) convertView
                .findViewById(R.id.widgetTitle);
        TextView dashboardNameText = (TextView) convertView
                .findViewById(R.id.widgetDashboard);
        WidgetInfo item = getItem(position);

        widgetTitleText.setText(item.title);
        dashboardNameText.setText("in dashboard " + item.dashboard);

        return convertView;
    }
}
