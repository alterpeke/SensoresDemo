package es.epinanab.sensores;

import java.util.ArrayList;

import es.epinanab.sensores.R;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TiemposDeSensor extends ListActivity {
	
	ArrayList<String> lista_estados;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
		setTitle("Tiempos para cada posicin");
		
		lista_estados = this.getIntent().getStringArrayListExtra("lista_estados");
		
		PosicionAdapter adaptador_fileitem = new PosicionAdapter(this, android.R.layout.simple_list_item_1, lista_estados);
		
		try {
			setListAdapter(adaptador_fileitem);
		} catch (Exception e) {
			Log.e("ERROR", "No se puede visualiar el listview: " + e.toString());
		}
		
	}

	class PosicionAdapter extends ArrayAdapter<String>{

		private int textViewResourdeId;

		public PosicionAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			
			this.textViewResourdeId = textViewResourceId;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(textViewResourdeId, null);
			}
			
			String s = getItem(position);
			
			TextView text = (TextView) convertView.findViewById(android.R.id.text1);
			text.setText(s);
			
			return convertView;
		}
	}
	
}