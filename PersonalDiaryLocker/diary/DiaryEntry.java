package diary;

public class DiaryEntry 
{
    private String title;
    private String content;

    public DiaryEntry(String title, String content) 
    {
        this.title = title;
        this.content = content;
    }

    public String getTitle() 
    { 
	return title; 
    }

    public String getContent() 
    { 
	return content; 
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}
