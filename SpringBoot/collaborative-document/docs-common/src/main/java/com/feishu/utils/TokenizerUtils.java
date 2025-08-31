package com.feishu.utils;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TokenizerUtils {
    private static final IKAnalyzer SMART_ANALYZER = new IKAnalyzer(true);
    private static final IKAnalyzer FINE_ANALYZER = new IKAnalyzer(false);

    // 返回混合分词结果（智能+细粒度）
    public static Set<String> tokenize(String text) throws IOException {
        Set<String> tokens = new HashSet<>();
        tokens.addAll(tokenizeInternal(SMART_ANALYZER, text));
        tokens.addAll(tokenizeInternal(FINE_ANALYZER, text));
        return tokens;
    }

    private static List<String> tokenizeInternal(IKAnalyzer analyzer, String text) throws IOException {
        List<String> tokens = new ArrayList<>();
        TokenStream stream = analyzer.tokenStream("", new StringReader(text));
        CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
            tokens.add(term.toString());
        }
        stream.close();
        return tokens;
    }
}
