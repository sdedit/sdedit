package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;

public abstract class TagletWriter {
	
	public TagletWriter() {
	}

	public abstract TagletOutput getOutputInstance();

	protected abstract TagletOutput getDocRootOutput();

	protected abstract TagletOutput deprecatedTagOutput(Doc paramDoc);

	protected abstract TagletOutput getParamHeader(String paramString);

	protected abstract TagletOutput returnTagOutput(Tag paramTag);

	protected abstract TagletOutput simpleTagOutput(Tag[] paramArrayOfTag,
			String paramString);

	protected abstract TagletOutput simpleTagOutput(Tag paramTag,
			String paramString);

	protected abstract TagletOutput getThrowsHeader();

	public abstract TagletOutput commentTagsToOutput(Tag paramTag,
			Tag[] paramArrayOfTag);

	public abstract TagletOutput commentTagsToOutput(Doc paramDoc,
			Tag[] paramArrayOfTag);

	public abstract TagletOutput commentTagsToOutput(Tag paramTag,
			Doc paramDoc, Tag[] paramArrayOfTag, boolean paramBoolean);

	public abstract TagletOutput getTagletOutputInstance();
}
