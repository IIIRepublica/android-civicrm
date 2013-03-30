package org.upsam.civicrm.util;



import org.upsam.civicrm.R;
import org.upsam.civicrm.beans.DataCivi;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Clase de utilidades
 * 
 * @author Equipo 7
 * Universidad Pontificia de Salamanca
 * v1.0
 *
 */
public class Utilities {
	
	public static String APY_KEY = "api_key";
	public static String KEY = "key";
	public static String BASE_URI = "base_uri";
	
	/**
	 * Mostrar el progress dialog customizado
	 * @param progressDialog
	 * @param context
	 * @param mensajeInformativo
	 * @return
	 */
	public static ProgressDialog showLoadingProgressDialog(ProgressDialog progressDialog,Context context,String mensajeInformativo) {
		return showProgressDialog(progressDialog,context,mensajeInformativo);
	}

	/**
	 * se encarga de mostrar el progress customizado
	 * @param progressDialog
	 * @param context
	 * @param mensajeInformativo
	 */
	private static ProgressDialog showProgressDialog(ProgressDialog progressDialog,Context context,String mensajeInformativo) {
		
		if (progressDialog == null) 
		{
			progressDialog = createCustomProgressDialog(context,mensajeInformativo);				
		}		
		progressDialog.show();	
		
		return progressDialog;
	}

	/**
	 * Cerrar el progress dialog customizado
	 * @param progressDialog
	 * @param destroyed
	 */
	public static void dismissProgressDialog(ProgressDialog progressDialog,boolean destroyed) {
		if (progressDialog != null && !destroyed) {
			progressDialog.dismiss();
		}
	}
	
	public static void dismissProgressDialog(ProgressDialog progressDialog) {
		if (progressDialog != null ) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * Obtencion de objeto con los datos del login
	 * @param applicationContext
	 * @return
	 */
	public static DataCivi getDataCivi(Context applicationContext)
	{
		DataCivi data = new DataCivi();		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);		
		data.setBase_url(cleanUrl(prefs.getString("PREF_URL", null))+"/sites/all/modules/civicrm/extern/rest.php?json=1&sequential=1");
		data.setSite_key(prefs.getString("PREF_SITE_KEY", null));		
		data.setUser_name(prefs.getString("PREF_USER", null));
		data.setPassword(prefs.getString("PREF_PASSWORD", null));		
		data.setApi_key(prefs.getString("PREF_API_KEY", null));		
		data.setKey(prefs.getString("PREF_USER_KEY", null));								
		return data;
	}
	
	/**
	 * Comprobacion si los datos estan cargados
	 * @param context
	 * @return
	 */
	public static boolean isInformationLoad(Activity context)
	{
		  SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		  if(mySharedPreferences.getString("PREF_API_KEY", "")!=null && !"".equalsIgnoreCase(mySharedPreferences.getString("PREF_API_KEY", "")))
		  {			  
			  return Boolean.TRUE.booleanValue();
		  }
		  else
		  {
			  return Boolean.FALSE.booleanValue();
		  }
	}		
	
	/**
	 * asegurar url incluida por usuario no tiene la barra- sino se le quita
	 * @param url
	 * @return
	 */
	public static String cleanUrl(String url)
	{
		String resultado = url;
		if(url!=null && !"".equalsIgnoreCase(url) && url.charAt(url.length()-1)=='/')
		{
			resultado = url.substring(0,url.length()-1);
		}
		return resultado;
	}
	
	/**
	 * Custom dialog que muestra un mensaje y se cierra
	 * @param context
	 * @param title
	 * @param body
	 * @param button
	 */
	public static void createCustomDialog(Context context,String title,String body,String button)
	{
		final Dialog dialog = new Dialog(context);
		
		//quitamos titulo ya que lo pintamos nosotros
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_dialog_acercade);		

		// establecer los componentes - text, image and button
		TextView textTitle = (TextView) dialog.findViewById(R.id.titleDialogAcerca);
		textTitle.setText(title);
		
		TextView textBody = (TextView) dialog.findViewById(R.id.textDialogAcerca);
		textBody.setText(body);
		
		Button dialogButton = (Button) dialog.findViewById(R.id.botonDialogAcerca);
		dialogButton.setText(button);
		// cerrar el dialogo
		dialogButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});

		dialog.show();
	}
		
	/**
	 * Custom Progress. ATENCION: el show o close se controla con el objeto devuelto
	 * @param context
	 * @param body
	 * @return
	 */
	public static ProgressDialog createCustomProgressDialog(Context context,String body)
	{
		ProgressDialog progressDialog = new ProgressDialog(context,R.style.progressDialogTheme);
		progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);		
		progressDialog.setMessage(body);
		progressDialog.setIndeterminate(Boolean.TRUE);
		return progressDialog;		 
	}

}
