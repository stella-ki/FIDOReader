/*
Copyright 2014  Jose Maria ARROYO jm.arroyo.castejon@gmail.com

APDUSenderContactLess is free software: you can redistribute it and/or modify
it  under  the  terms  of the GNU General Public License  as published by the 
Free Software Foundation, either version 3 of the License, or (at your option) 
any later version.

APDUSenderContactLess is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package com.syki.fidoreader;


import java.io.IOException;
import java.util.Arrays;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Parcelable;
import com.syki.fidoreader.R;

public class ApduSenderContactLess extends Activity
{

    static byte[] byteAPDU=null;
    static byte[] respAPDU=null;

    static HexadecimalKbd mHexKbd;

    private static CheckBox mCheckResp;

    private Button mSendAPDUButton;
    private Button mClearLogButton;

    static ImageView icoNfc;
    static ImageView icoCard;
    
    static TextView TextNfc;
    static TextView TextCard;

    static TextView txtLog;


    private NfcAdapter mAdapter=null;
    private PendingIntent mPendingIntent;
    private String[][] mTechLists;
    private IntentFilter[] mFilters;
    static IsoDep myTag;
    boolean mFirstDetected=false;
    boolean mShowAtr=false;



    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        byteAPDU=null;
        respAPDU=null;

        mHexKbd= new HexadecimalKbd(this, R.id.keyboardview, R.xml.hexkbd );

        txtLog = (TextView) findViewById(R.id.textLog);
        icoNfc = (ImageView) findViewById(R.id.imageNfc);
        icoNfc.setImageResource(R.drawable.ic_nfc_off);
        icoCard = (ImageView) findViewById(R.id.imageCard);
        icoCard.setImageResource(R.drawable.ic_icc_off);
        TextNfc = (TextView) findViewById(R.id.textNfc);
        TextCard = (TextView) findViewById(R.id.textCard);

        mSendAPDUButton = (Button) findViewById(R.id.button_SendApdu);
        mSendAPDUButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if( mFirstDetected==true && myTag.isConnected())
                {
                    if(mShowAtr==true)
                    {
                        icoCard.setImageResource(R.drawable.ic_icc_on_atr);
                    }
                    else
                    {
                        icoCard.setImageResource(R.drawable.ic_icc_on);
                    }
                    clearlog();
                    if(!bSendAPDU())
                    {
                        vShowErrorVaules();
                    }
                }
                else
                {
                    icoCard.setImageResource(R.drawable.ic_icc_off);
                    clearlog();
                    TextCard.setText("PLEASE TAP CARD");
                    mSendAPDUButton.setEnabled(false);
                }
            }
        });


        mClearLogButton = (Button) findViewById(R.id.button_ClearLog);
        mClearLogButton.setOnClickListener(new OnClickListener() 
        {
            @Override
            public void onClick(View v) 
            {
                if( mFirstDetected==true && myTag.isConnected() ) 
                {
                    if(mShowAtr==true)
                    {
                        icoCard.setImageResource(R.drawable.ic_icc_on_atr);
                    }
                    else
                    {
                        icoCard.setImageResource(R.drawable.ic_icc_on);
                    }
                }
                else
                {
                    icoCard.setImageResource(R.drawable.ic_icc_off);
                    TextCard.setText("PLEASE TAP CARD");
                    mSendAPDUButton.setEnabled(false);
                }
                clearlog();
            }
        });


        mCheckResp = (CheckBox) findViewById(R.id.check_box_resp);
        mCheckResp.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if( (mFirstDetected==true) && (myTag.isConnected()) );
                else
                {
                    icoCard.setImageResource(R.drawable.ic_icc_off);
                    clearlog();
                    mSendAPDUButton.setEnabled(false);
                    TextCard.setText("PLEASE TAP CARD");
                    return;
                }
                if( (byteAPDU==null)||(respAPDU==null) )
                {
                    return;
                }
                if( isChecked )
                {
                    clearlog();
                    print("***COMMAND APDU***");
                    print("");
                    try
                    {
                        print("IFD - " + getHexString(byteAPDU));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        print("");
                        print("ICC - " + getHexString(respAPDU));
                    }
                    catch (Exception e) 
                    {
                        e.printStackTrace();
                    }
                    if(mCheckResp.isChecked())
                    {
                        try
                        {
                            vShowResponseInterpretation(respAPDU);
                        }
                        catch (Exception e) 
                        {
                            clearlog();
                            print("Response is not TLV format !!!");
                        }
                    }
                    
                }
                else
                {
                    clearlog();
                    print("***COMMAND APDU***");
                    print("");
                    try 
                    {
                        print("IFD - " + getHexString(byteAPDU));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    try 
                    {
                        print("");
                        print("ICC - " + getHexString(respAPDU));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        mSendAPDUButton.setEnabled(false);
        mClearLogButton.setEnabled(true);


        resolveIntent(getIntent());

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        try
        {
            ndef.addDataType("*/*");
        }
        catch (MalformedMimeTypeException e)
        {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] { ndef, };
        mTechLists = new String[][] { new String[] { IsoDep.class.getName() } };
    }


    @Override
    public void onResume()
    {
        super.onResume();

        byteAPDU=null;
        respAPDU=null;

        if(  (mFirstDetected==true) && (myTag.isConnected()) )
        {
            if(mShowAtr==true)
            {
                icoCard.setImageResource(R.drawable.ic_icc_on_atr);
            }
            else
            {
                icoCard.setImageResource(R.drawable.ic_icc_on);
            }
        }
        else
        {
            icoCard.setImageResource(R.drawable.ic_icc_off);
        }
        if( (mAdapter == null) || (!mAdapter.isEnabled()) )
        {
            if (mAdapter == null)
            {
                clearlog();
                TextCard.setText("PLEASE TAP CARD");
                mSendAPDUButton.setEnabled(false);
                print("    No NFC hardware found.");
                print("    Program will NOT function.");
            }
            else if(mAdapter.isEnabled())
            {
                clearlog();
                TextNfc.setText("NFC ENABLED");
            }
            else
            {
                clearlog();
                TextCard.setText("PLEASE TAP CARD");
                mSendAPDUButton.setEnabled(false);
                print("    NFC hardware has been disabled.");
                print("    Please enable it first.");
                icoNfc.setImageResource(R.drawable.ic_nfc_off);
                TextNfc.setText("NO READER DETECTED");
            }
        }
        if (mAdapter != null)
        {
            if (mAdapter.isEnabled())
            {
                clearlog();
                TextNfc.setText("NFC ENABLED");
                icoNfc.setImageResource(R.drawable.ic_nfc_on);
                print("This program is distributed in the hope that it will be useful for educational purposes.  Enjoy! ");
            }
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
        else
        {
            clearlog();
            icoNfc.setImageResource(R.drawable.ic_nfc_off);
            TextNfc.setText("NO READER DETECTED");
            TextCard.setText("PLEASE TAP CARD");
            mSendAPDUButton.setEnabled(false);
            print("    No NFC hardware found.");
            print("    Program will NOT function.");
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        
        byteAPDU=null;
        respAPDU=null;
        
        if( (mFirstDetected==true) && (myTag.isConnected()) )
        {
            if(mShowAtr==true)
            {
                icoCard.setImageResource(R.drawable.ic_icc_on_atr);
            }
            else
            {
                icoCard.setImageResource(R.drawable.ic_icc_on);
            }
        }
        else
        {
            icoCard.setImageResource(R.drawable.ic_icc_off);
        }
        mAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        setIntent(intent);
        resolveIntent(intent);
    }

    @Override public void onBackPressed()
    {
        if( mHexKbd.isCustomKeyboardVisible() ) mHexKbd.hideCustomKeyboard(); else this.finish();
    }
    
    public static void HideKbd() 
    {
        if( mHexKbd.isCustomKeyboardVisible() ) mHexKbd.hideCustomKeyboard(); 
    }

    private static void clearlog()
    {
        txtLog.setText("");
    }

    private static void print(String s) 
    {
        txtLog.append(s);
        txtLog.append("\r\n");
        return;
    }

    private static byte[]  transceives (byte[] data)
    {
        byte[] ra = null;
        
        try 
        {
            print("***COMMAND APDU***");
            print("");
            print("IFD - " + getHexString(data));
        } 
        catch (Exception e1) 
        {
            e1.printStackTrace();
        }

        try 
        {
            ra = myTag.transceive(data);
        }
        catch (IOException e)
        {

            print("************************************");
            print("         NO CARD RESPONSE");
            print("************************************");

        }
        try
        {
            print("");
            print("ICC - " + getHexString(ra));
        }
        catch (Exception e1) 
        {
            e1.printStackTrace();
        }

        return (ra);
    }
      
    private static boolean bSendAPDU() 
    {
        HideKbd();

        String StringAPDU = "00A4040007A0000000032010";


        byteAPDU = atohex(StringAPDU);
        respAPDU = transceives(byteAPDU);

        if(mCheckResp.isChecked())
        {
            try
            {
                vShowResponseInterpretation(respAPDU);
            }
            catch (Exception e) 
            {
                clearlog();
                print("Response is not TLV format !!!");
            }
            
        }

        return true;
    }

    private void resolveIntent(Intent intent) 
    {
        String action = intent.getAction();
        clearlog();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            final Tag t = (Tag) tag;
            myTag = IsoDep.get(t);
            mFirstDetected=true;
            if( !myTag.isConnected() )
            {
                try
                {
                    myTag.connect();
                    myTag.setTimeout(5000);
                }
                catch (IOException e) 
                {
                    e.printStackTrace();
                    return;
                }
            }
            if( myTag.isConnected() )
            {
                if(mShowAtr==true)
                {
                    icoCard.setImageResource(R.drawable.ic_icc_on_atr);
                }
                else
                {
                    icoCard.setImageResource(R.drawable.ic_icc_on);
                }
                vShowCardRemovalInfo();
                String szATR = null;
                try
                {
                    mShowAtr=true;
                    szATR =" 3B " + getATRLeString(myTag.getHistoricalBytes())+ "80 01 " + getHexString(myTag.getHistoricalBytes())+""+ getATRXorString(myTag.getHistoricalBytes());
                } 
                catch (Exception e) 
                {
                    mShowAtr=false;
                    szATR = "CARD DETECTED  ";
                }
                TextCard.setText(szATR);

                mSendAPDUButton.setEnabled(true);
                clearlog(); 

                mCheckResp.setChecked(false);
            }
            else
            {
                icoCard.setImageResource(R.drawable.ic_icc_off);
            }
        }
        if( mFirstDetected==true && myTag.isConnected() ) 
        {
            if(mShowAtr==true)
            {
                icoCard.setImageResource(R.drawable.ic_icc_on_atr);
            }
            else
            {
                icoCard.setImageResource(R.drawable.ic_icc_on);
            }
        }
        else
        {
            icoCard.setImageResource(R.drawable.ic_icc_off);
        }
    }

    private void vSetBuiltinCommand()
    {
    	clearlog();

        return;
    }

    private static void vShowResponseInterpretation(byte[] data)
    {

        print("");
        print("====================================");
        print("RESPONSE INTERPRETATION:");


        if (data.length > 2)
        {
            byte[] sw12 = new byte[2];
            System.arraycopy(data, data.length-2, sw12, 0, 2);
            byte[] payload = Arrays.copyOf(data, (data.length)-2 );
            try
            {
                print("SW1-SW2 " + getHexString(sw12) + RetStatusWord.getSWDescription(Util.szByteHex2String(sw12[0]) + Util.szByteHex2String(sw12[1])));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            print(EmvInterpreter.ShowEMV_Interpretation(payload));

        }
        else if (data.length == 2)
        {
            byte[] sw12 = new byte[2];
            System.arraycopy(data, data.length-2, sw12, 0, 2);
            try
            {
                print("SW1-SW2 " + getHexString(sw12) );
                print(RetStatusWord.getSWDescription(Util.szByteHex2String(sw12[0]) + Util.szByteHex2String(sw12[1])));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        print("====================================");
        return;
    }

    private void vShowCardRemovalInfo()
    {
        Context context = getApplicationContext();
        CharSequence text = "Card Removal will NOT be detected";
        int duration = Toast.LENGTH_LONG;
        HideKbd();
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void vShowGeneralMesg(String szText)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, szText, duration);
        toast.show();
    }

    private void vShowErrorVaules()
    {
        Context context = getApplicationContext();
        CharSequence text = "C-APDU values ERROR";
        int duration = Toast.LENGTH_LONG;
        HideKbd();
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private static String getHexString(byte[] data) throws Exception
    {
        String szDataStr = "";
        for (int ii=0; ii < data.length; ii++) 
        {
            szDataStr += String.format("%02X ", data[ii] & 0xFF);
        }
        return szDataStr;
    }

    private static String getATRLeString(byte[] data) throws Exception
    {
        return String.format("%02X ", data.length | 0x80);
    }

    private static String getATRXorString(byte[] b) throws Exception
    {
        int Lrc=0x00;
        Lrc = b.length | 0x80;
        Lrc = Lrc^0x81;
        for (int i=0; i < b.length; i++) 
        {
            Lrc = Lrc^(b[i] & 0xFF);
        }
        return String.format("%02X ", Lrc);
    }

    private static byte[] atohex(String data)
    {
        String hexchars = "0123456789abcdef";

        data = data.replaceAll(" ","").toLowerCase();
        if (data == null)
        {
            return null;
        }
        byte[] hex = new byte[data.length() / 2];
        
        for (int ii = 0; ii < data.length(); ii += 2)
        {
            int i1 = hexchars.indexOf(data.charAt(ii));
            int i2 = hexchars.indexOf(data.charAt(ii + 1));
            hex[ii/2] = (byte)((i1 << 4) | i2);
        }
        return hex;
    }

}

