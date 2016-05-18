package es.jtresaco.apps.vocabularyquiz;

/**
 * Created by javier on 5/18/16.
 */
public class Word {
    private String mOriginal;
    private String mOriginalAlt;
    private String mTranslation;
    private String mTranslationAlt;
    private String mLesson;

    public String getOriginal() {
        return mOriginal;
    }

    public void setOriginal(String original) {
        this.mOriginal = original;
    }

    public String getOriginalAlt() {
        return mOriginalAlt;
    }

    public void setOriginalAlt(String originalAlt) {
        this.mOriginalAlt = originalAlt;
    }

    public String getTranslation() {
        return mTranslation;
    }

    public void setTranslation(String translation) {
        this.mTranslation = translation;
    }

    public boolean hasTranslationAlt() {
        return mTranslationAlt.length()!=0;
    }

    public String getTranslationAlt() {
        return mTranslationAlt;
    }

    public void setTranslationAlt(String translationAlt) {
        this.mTranslationAlt = translationAlt;
    }

    public String getLesson() {
        return mLesson;
    }

    public void setLesson(String lesson) {
        this.mLesson = lesson;
    }
}
