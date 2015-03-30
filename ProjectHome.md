uses <a href='http://maps.google.com'><a href='http://maps.google.com'>http://maps.google.com</a></a> to route a driving directions between two locations and parse response <a href='http://code.google.com/intl/uk/apis/kml/documentation/'>kml</a> file with SAX parser into easy coordinates structure. Contains example applications on Blackberry and Android.

<h3>UPDATE</h3>
Feb 22, 2012 - Update to handle multiple 

&lt;coordinates&gt;

 tags per kml document. Thanks, Lukas!

<h3>WARNING</h3>
<p><i>This code samples are for educational purpose only!</i></p>
<p>It's not officially allowed by <a href='http://code.google.com/intl/uk/apis/maps/terms.html'>Google Maps APIs Terms of Service</a> to use Google Maps API for driving directions:</p>
<blockquote>
<p>Google Maps/Google Earth APIs Terms of Service</p>
<p>Last updated: May 27, 2009</p>
<p>...</p>
<p>10. License Restrictions. Except as expressly permitted under the Terms, or unless you have received prior written authorization from Google (or, as applicable, from the provider of particular Content), Google's licenses above are subject to your adherence to all of the restrictions below. Except as explicitly permitted in Section 7 or the Maps APIs Documentation, you must not (nor may you permit anyone else to):</p>
<p>...</p>
<p>10.9 use the Service or Content with any products, systems, or applications for or in connection with:</p>
<p>(a) real time navigation or route guidance, including but not limited to turn-by-turn route guidance that is synchronized to the position of a user's sensor-enabled device;</p>
</blockquote>
and may be disabled for certain apps (somehow, at least on Android)... From <a href='http://groups.google.com/group/Google-Maps-API/msg/7fa25a13e6153d02'>Geocode scraping in .NET conversation</a>:
<blockquote>
<p>This is not allowed by the API terms of use.  You should not scrape<br>
Google Maps to generate geocodes.  We will block services that do<br>
automated queries of our servers.</p>
<p>Bret Taylor</p>
<p>Product Manager, Google Maps</p>
</blockquote>