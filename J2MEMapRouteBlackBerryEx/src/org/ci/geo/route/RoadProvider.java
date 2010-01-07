package org.ci.geo.route;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RoadProvider {

	public static Road getRoute(double fromLat, double fromLon, double toLat,
			double toLon) {
		InputStream is = getPath(fromLat, fromLon, toLat, toLon);
		KMLHandler handler = new KMLHandler();
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(is, handler);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return handler.mRoad;
	}

	private static InputStream getPath(double fromLat, double fromLon,
			double toLat, double toLon) {// connect to map web service
		InputStream is = null;
		StringBuffer urlString = new StringBuffer();
		urlString.append("http://maps.google.com/maps?f=d&hl=en");
		urlString.append("&saddr=");// from
		urlString.append(Double.toString(fromLat));
		urlString.append(",");
		urlString.append(Double.toString(fromLon));
		urlString.append("&daddr=");// to
		urlString.append(Double.toString(toLat));
		urlString.append(",");
		urlString.append(Double.toString(toLon));
		urlString.append("&ie=UTF8&0&om=0&output=kml");
		HttpConnection urlConnection = null;
		String url = urlString.toString();
		try {
			urlConnection = (HttpConnection) Connector.open(url);
			urlConnection.setRequestMethod("GET");
			is = urlConnection.openInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}

}

class KMLHandler extends DefaultHandler {
	Road mRoad;
	boolean isPlacemark;
	boolean isRoute;
	boolean isItemIcon;
	private Stack mCurrentElement = new Stack();

	public KMLHandler() {
		mRoad = new Road();
	}

	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		mCurrentElement.push(name);
		if (name.equalsIgnoreCase("Placemark")) {
			isPlacemark = true;
			mRoad.mPoints = addPoint(mRoad.mPoints);
		} else if (name.equalsIgnoreCase("ItemIcon")) {
			if (isPlacemark)
				isItemIcon = true;
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String chars = new String(ch, start, length).trim();
		String name = (String) mCurrentElement.peek();
		if (chars.length() > 0) {
			if (name.equalsIgnoreCase("name")) {
				if (isPlacemark) {
					isRoute = chars.equalsIgnoreCase("Route");
					if (!isRoute) {
						mRoad.mPoints[mRoad.mPoints.length - 1].mName = chars;
					}
				} else {
					mRoad.mName = chars;
				}
			} else if (name.equalsIgnoreCase("color") && !isPlacemark) {
				mRoad.mColor = Integer.parseInt(chars, 16);
			} else if (name.equalsIgnoreCase("width") && !isPlacemark) {
				mRoad.mWidth = Integer.parseInt(chars);
			} else if (name.equalsIgnoreCase("description")) {
				if (isPlacemark) {
					String description = cleanup(chars);
					if (!isRoute)
						mRoad.mPoints[mRoad.mPoints.length - 1].mDescription = description;
					else
						mRoad.mDescription = description;
				}
			} else if (name.equalsIgnoreCase("href")) {
				if (isItemIcon) {
					mRoad.mPoints[mRoad.mPoints.length - 1].mIconUrl = chars;
				}
			} else if (name.equalsIgnoreCase("coordinates")) {
				if (isPlacemark) {
					if (!isRoute) {
						String[] xyParsed = split(chars, ",");
						double x = Double.parseDouble(xyParsed[0]);
						double y = Double.parseDouble(xyParsed[1]);
						mRoad.mPoints[mRoad.mPoints.length - 1].mLatitude = x;
						mRoad.mPoints[mRoad.mPoints.length - 1].mLongitude = y;
					} else {
						String[] coodrinatesParsed = split(chars, " ");
						for (int i = 0; i < coodrinatesParsed.length; i++) {
							double[] xy = new double[] {};
							String[] xyParsed = split(coodrinatesParsed[i], ",");
							double x = Double.parseDouble(xyParsed[0]);
							double y = Double.parseDouble(xyParsed[1]);

							xy = addDouble(xy, x);
							xy = addDouble(xy, y);
							mRoad.mRoute = addDouble(mRoad.mRoute, xy);
						}
					}
				}
			}
		}
	}

	private String cleanup(String value) {
		String remove = "<br/>";
		int index = value.indexOf(remove);
		if (index != -1)
			value = value.substring(0, index);
		remove = "&#160;";
		index = value.indexOf(remove);
		int len = remove.length();
		while (index != -1) {
			value = value.substring(0, index).concat(
					value.substring(index + len, value.length()));
			index = value.indexOf(remove);
		}
		return value;
	}

	public void endElement(String uri, String localName, String name)
			throws SAXException {
		mCurrentElement.pop();
		if (name.equalsIgnoreCase("Placemark")) {
			isPlacemark = false;
			if (isRoute)
				isRoute = false;
		} else if (name.equalsIgnoreCase("ItemIcon")) {
			if (isItemIcon)
				isItemIcon = false;
		}
	}

	public Point[] addPoint(Point[] points) {
		Point[] result = new Point[points.length + 1];
		for (int i = 0; i < points.length; i++)
			result[i] = points[i];
		result[points.length] = new Point();
		return result;
	}

	static double[] addDouble(double[] array, double element) {
		int arrayLength = array.length;
		double[] result = new double[arrayLength + 1];
		for (int i = 0; i < arrayLength; i++)
			result[i] = array[i];
		result[arrayLength] = element;
		return result;
	}

	static double[][] addDouble(double[][] array, double[] element) {
		int arrayLength = array.length;
		double[][] result = new double[arrayLength + 1][];
		for (int i = 0; i < arrayLength; i++) {
			int elementLength = array[i].length;
			result[i] = new double[elementLength];
			for (int j = 0; j < elementLength; j++)
				result[i][j] = array[i][j];
		}
		int newElementLength = element.length;
		result[arrayLength] = new double[newElementLength];
		for (int j = 0; j < newElementLength; j++)
			result[arrayLength][j] = element[j];
		return result;
	}

	private static String[] split(String strString, String strDelimiter) {
		String[] strArray;
		int iOccurrences = 0;
		int iIndexOfInnerString = 0;
		int iIndexOfDelimiter = 0;
		int iCounter = 0;
		if (strString == null) {
			throw new IllegalArgumentException("Input string cannot be null.");
		}
		if (strDelimiter.length() <= 0 || strDelimiter == null) {
			throw new IllegalArgumentException(
					"Delimeter cannot be null or empty.");
		}
		if (strString.startsWith(strDelimiter)) {
			strString = strString.substring(strDelimiter.length());
		}
		if (!strString.endsWith(strDelimiter)) {
			strString += strDelimiter;
		}
		while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
				iIndexOfInnerString)) != -1) {
			iOccurrences += 1;
			iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
		}
		strArray = new String[iOccurrences];
		iIndexOfInnerString = 0;
		iIndexOfDelimiter = 0;
		while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
				iIndexOfInnerString)) != -1) {
			strArray[iCounter] = strString.substring(iIndexOfInnerString,
					iIndexOfDelimiter);
			iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
			iCounter += 1;
		}

		return strArray;
	}
}