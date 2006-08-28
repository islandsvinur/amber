
package amber.sieve;

import com.cmlabs.air.*;

public class KeywordSpotter {

    private JavaAIRPlug plug;

    public KeywordSpotter() {
        System.out.println("Keyword spotter created");

        plug = new JavaAIRPlug("KeywordSpotter", "localhost", 10000);
        plug.init();

        String xml = "<module name=\"JavaPoster\"><post to=\"WB1\" type=\"My.Test.Message.Type\" /></module>";

        plug.sendRegistration(xml);

        plug.postOutputMessage(new Message());
    }

}
