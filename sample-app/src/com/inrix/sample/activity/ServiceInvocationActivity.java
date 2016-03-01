/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.Error;
import com.inrix.sdk.IDataResponseListener;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.GeoPoint;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Provides a sample of service invocation.
 */
public class ServiceInvocationActivity extends InrixSdkActivity {
    private TextView result;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_service_invocation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.findViewById(R.id.service_invocation_invoke).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeService();
            }
        });

        this.result = (TextView) this.findViewById(R.id.service_invocation_result);
    }

    /**
     * Invoke a service and get the result.
     */
    private void invokeService() {
        final String apiName = "Mobile.TMC.Radius";
        final Map<String, Object> arguments = new HashMap<>();
        final GeoPoint center = new GeoPoint(47.643365, -122.203479);
        arguments.put("center", center);
        arguments.put("radius", 5);

        InrixCore.invokeService(apiName, arguments, center, new IDataResponseListener<String>() {
            @Override
            public void onResult(final String data) {
                result.setText(format(data));
            }

            @Override
            public void onError(final Error error) {
                result.setText(error.toString());
            }
        });
    }

    public String format(String xml) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(parseXmlFile(xml));
            transformer.transform(source, result);

            return result.getWriter().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
