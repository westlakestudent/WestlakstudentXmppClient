package com.westlakstudentxmppclient.notification;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 *
 * NotificationIQProvider
 * @author chendong
 * 2014年10月27日 下午5:34:59
 * @version 1.0.0
 *
 */
public class NotificationIQProvider  implements IQProvider{

	public NotificationIQProvider() {
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {

        NotificationIQ notification = new NotificationIQ();
        for (boolean done = false; !done;) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if ("id".equals(parser.getName())) {
                    notification.setId(parser.nextText());
                }
                if ("imei".equals(parser.getName())) {
                    notification.setImei(parser.nextText());
                }
                if ("title".equals(parser.getName())) {
                    notification.setTitle(parser.nextText());
                }
                if ("message".equals(parser.getName())) {
                    notification.setMessage(parser.nextText());
                }
                if ("remark".equals(parser.getName())) {
                    notification.setRemark(parser.nextText());
                }
            } else if (eventType == XmlPullParser.END_TAG
                    && "notification".equals(parser.getName())) {
                done = true;
            }
        }

        return notification;
    }
}
