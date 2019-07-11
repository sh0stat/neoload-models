package com.neotys.neoload.model.readers.jmeter;

import com.neotys.neoload.model.listener.EventListener;
import com.neotys.neoload.model.v3.project.userpath.Request;
import com.neotys.neoload.model.v3.project.userpath.VariableExtractor;
import org.apache.jmeter.extractor.RegexExtractor;
import org.apache.jorphan.collections.HashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RegularExtractorConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegularExtractorConverter.class);
    private static EventListener eventListener ;

    RegularExtractorConverter(EventListener eventListener) {
        RegularExtractorConverter.eventListener = eventListener;}

    static void extract(Request.Builder req, HashTree subTree) {

        for (Object o : subTree.list()) {
            if (o instanceof RegexExtractor) {
                VariableExtractor.Builder variableExtractor = VariableExtractor.builder();
               RegexExtractor regexExtractor = (RegexExtractor) o;
                variableExtractor.description(regexExtractor.getComment());
                variableExtractor.name(regexExtractor.getRefName());
                variableExtractor.template(regexExtractor.getTemplate());
                variableExtractor.matchNumber(regexExtractor.getMatchNumber());
                variableExtractor.regexp(regexExtractor.getRegex());
                checkApplyTo(regexExtractor);
                variableExtractor = convertBody(regexExtractor,variableExtractor);
                req.addExtractors(variableExtractor.build());
            }
        }
        LOGGER.info("Header on the RegexExtractor is a success");
        eventListener.readUnsupportedAction("RegexExtractorConverter");
    }

    private static VariableExtractor.Builder convertBody(RegexExtractor regexExtractor, VariableExtractor.Builder variableExtractor) {
        if (regexExtractor.useBody() || regexExtractor.useBodyAsDocument() || regexExtractor.useUnescapedBody()){
            variableExtractor.from(VariableExtractor.From.BODY);
        }
        else if (regexExtractor.useHeaders()){
            variableExtractor.from(VariableExtractor.From.HEADER);
        }
        else if(regexExtractor.useMessage()){
            variableExtractor.template("$2$");
            variableExtractor = VariableExtractor.builder();
            variableExtractor.name(regexExtractor.getRefName());
            variableExtractor.regexp("HTTP/\\d\\.\\d (\\d{3}) (.*)");
            variableExtractor.description(regexExtractor.getComment());
            variableExtractor.matchNumber(regexExtractor.getMatchNumber());
        }
        else if (regexExtractor.useCode()){
            variableExtractor.template("$1$");
            variableExtractor = VariableExtractor.builder();
            variableExtractor.name(regexExtractor.getRefName());
            variableExtractor.regexp("HTTP/\\d\\.\\d (\\d{3}) (.*)");
            variableExtractor.description(regexExtractor.getComment());
            variableExtractor.matchNumber(regexExtractor.getMatchNumber());
        }
        else{
            LOGGER.error("Not Supported for the convertion");
            eventListener.readUnsupportedParameter("RegexExtractor","Field to Check", "Request Header and URL");
        }
        return variableExtractor;
    }

    private static void checkApplyTo(RegexExtractor regexExtractor) {
        if ("all".equals(regexExtractor.fetchScope())){
            LOGGER.warn("We can't manage the sub-samples conditions");
            eventListener.readSupportedParameterWithWarn("RegexExtractor", "ApplyTo", "Main Sample & Sub-Sample","Can't check Sub-Sample");
        }
        else if("children".equals(regexExtractor.fetchScope())|| "variable".equals(regexExtractor.fetchScope())){
            LOGGER.error("We can't manage the sub-sample and Jmeter Variable Use, so we convert like main sample only");
            eventListener.readUnsupportedParameter("RegexExtractor","ApplyTo","Sub-Sample or Jmeter Variable");
        }
    }


}
