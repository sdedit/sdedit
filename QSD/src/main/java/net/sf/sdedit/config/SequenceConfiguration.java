//Copyright (c) 2006 - 2015, Markus Strauch.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.
package net.sf.sdedit.config;

import java.awt.Color;

import net.sf.sdedit.ui.components.configuration.Adjustable;

public interface SequenceConfiguration extends Configuration {

	public int getActivationBarBorderThickness();

	public int getActorWidth();

	public Color getArrowColor();

	public int getDestructorWidth();

	public int getFragmentBorderThickness();

	public Color getFragmentEdgeColor();

	public Color getFragmentLabelBgColor();

	public int getFragmentMargin();

	public int getFragmentPadding();

	public int getFragmentTextPadding();

	public int getGlue();

	public int getHeadHeight();

	public int getHeadLabelPadding();

	public int getHeadWidth();

	public Color getLabeledBoxBgColor();

	public int getLifelineThickness();

	public int getMainLifelineWidth();

	public int getMessageLabelSpace();
	
	public int getMessageLineLength();

	public int getMessagePadding();

	public Color getNoteBgColor();

	public int getNoteBorderThickness();

	public int getNoteMargin();

	public int getNotePadding();

	public int getSelfMessageHorizontalSpace();

	public int getSeparatorBottomMargin();

	public int getSeparatorTopMargin();

	public int getSpaceBeforeActivation();

	public int getSpaceBeforeAnswerToSelf();

	public int getSpaceBeforeConstruction();

	public int getSpaceBeforeSelfMessage();

	public int getSubLifelineWidth();

	public Color getTc0();

	public Color getTc1();

	public Color getTc2();

	public Color getTc3();

	public Color getTc4();

	public Color getTc5();

	public Color getTc6();

	public Color getTc7();

	public Color getTc8();

	public Color getTc9();

	public boolean isColorizeThreads();

	public boolean isExplicitReturns();

	public boolean isOpaqueMessageText();

	public boolean isReturnArrowVisible();

	public boolean isReuseSpace();

	public boolean isShouldShadowParticipants();

	public boolean isThreaded();

	public boolean isThreadNumbersVisible();

	@Adjustable(editable = true, info = "Activation bar border thickness", category = "Line thickness", min = 1)
	public void setActivationBarBorderThickness(int activationBarBorderThickness);

	@Adjustable(dflt = 25, min = 20, max = 100, info = "Actor width", category = "Lifelines")
	public void setActorWidth(int actorWidth);

	@Adjustable(info = "Arrow colour", category = "Colours")
	public void setArrowColor(Color c);

	@Adjustable(depends = "threaded=true", info = "Colourize threads", category = "Threads")
	public void setColorizeThreads(boolean colorizeThreads);

	@Adjustable(dflt = 30, min = 5, max = 100, info = "Destructor cross width", category = "Misc")
	public void setDestructorWidth(int destructorWidth);

	@Adjustable(editable = false, info = "Require explicit returns", category = "Misc")
	public void setExplicitReturns(boolean explicitReturns);

	@Adjustable(editable = true, info = "Fragment border thickness", category = "Line thickness", min = 1)
	public void setFragmentBorderThickness(int fragmentBorderThickness);

	@Adjustable(info = "Fragment edge colour", category = "Colours")
	public void setFragmentEdgeColor(Color c);

	@Adjustable(info = "Fragment label background colour", category = "Colours")
	public void setFragmentLabelBgColor(Color c);

	@Adjustable(dflt = 8, min = 0, max = 100, editable = true, info = "Fragment margin", category = "Fragments")
	public void setFragmentMargin(int fragmentMargin);

	@Adjustable(dflt = 10, min = 1, max = 100, editable = true, info = "Fragment padding", category = "Fragments")
	public void setFragmentPadding(int commentPadding);

	@Adjustable(dflt = 3, min = 1, max = 100, editable = true, info = "Fragment label padding", category = "Fragments")
	public void setFragmentTextPadding(int commentTextPadding);

	@Adjustable(dflt = 10, min = 0, max = 999, info = "Glue", category = "Misc")
	public void setGlue(int glue);

	@Adjustable(dflt = 35, min = 20, max = 100, info = "Head height", category = "Lifelines")
	public void setHeadHeight(int headHeight);

	@Adjustable(dflt = 5, min = 1, max = 100, editable = true, info = "Head label padding", category = "Lifelines")
	public void setHeadLabelPadding(int headLabelPadding);

	@Adjustable(dflt = 100, min = 50, max = 300, step = 1, info = "Head width", category = "Lifelines")
	public void setHeadWidth(int headWidth);

	@Adjustable(info = "Head background colour", category = "Colours")
	public void setLabeledBoxBgColor(Color c);

	@Adjustable(editable = true, info = "(Inactive) Lifeline thickness", category = "Line thickness", min = 1)
	public void setLifelineThickness(int lifelineThickness);

	@Adjustable(dflt = 8, min = 2, max = 50, info = "Activation bar width (1st level)", category = "Lifelines")
	public void setMainLifelineWidth(int width);

	@Adjustable(dflt = 3, min = 1, max = 100, editable = true, info = "Space below message label", category = "Vertical spaces")
	public void setMessageLabelSpace(int messageLabelSpace);

	@Adjustable(dflt = 6, min = 1, max = 100, info = "Message padding", category = "Messages")
	public void setMessagePadding(int messagePadding);
	
	@Adjustable(dflt = 0, min = 0, max = 100, info = "Message line length", category = "Messages")
	public void setMessageLineLength(int messageLineLength);

	@Adjustable(info = "Note background colour", category = "Colours")
	public void setNoteBgColor(Color c);

	@Adjustable(editable = true, info = "Note border thickness", category = "Line thickness", min = 1)
	public void setNoteBorderThickness(int noteBorderThickness);

	@Adjustable(dflt = 6, min = 1, max = 20, info = "Note box margin", category = "Notes")
	public void setNoteMargin(int noteMargin);

	@Adjustable(dflt = 6, min = 1, max = 20, info = "Note box padding", category = "Notes")
	public void setNotePadding(int notePadding);

	@Adjustable(depends = "threaded=true,colorizeThreads=true", info = "Colourize message text background", category = "Threads")
	public void setOpaqueMessageText(boolean opaqueMessageText);

	@Adjustable(info = "Show dashed lines for return messages with no text", category = "Messages")
	public void setReturnArrowVisible(boolean visible);

	@Adjustable(dflt = 15, min = 10, max = 100, info = "Self-messages width", category = "Messages")
	public void setSelfMessageHorizontalSpace(int selfMessageHorizontalSpace);

	@Adjustable(dflt = 8, min = 1, max = 100, info = "Separator bottom margin", category = "Fragments")
	public void setSeparatorBottomMargin(int beforeFragmentText);

	@Adjustable(dflt = 15, min = 1, max = 100, info = "Separator top margin", category = "Fragments")
	public void setSeparatorTopMargin(int beforeSeparator);

	@Adjustable(info = "Put shadows on participants", category = "Lifelines")
	public void setShouldShadowParticipants(boolean shouldShadowParticipants);

	@Adjustable(dflt = 2, min = 0, max = 100, info = "Space before activation", category = "Vertical spaces")
	public void setSpaceBeforeActivation(int spaceBeforeActivation);

	@Adjustable(dflt = 10, min = 10, max = 100, editable = true, info = "Space before answer to self", category = "Vertical spaces")
	public void setSpaceBeforeAnswerToSelf(int spaceBeforeAnswerToSelf);

	@Adjustable(dflt = 6, min = 1, max = 100, info = "Space before constructor", category = "Vertical spaces")
	public void setSpaceBeforeConstruction(int spaceBeforeConstruction);

	@Adjustable(dflt = 7, min = 3, max = 100, info = "Space before message to self", category = "Vertical spaces")
	public void setSpaceBeforeSelfMessage(int spaceBeforeSelfMessage);

	@Adjustable(dflt = 6, min = 2, max = 100, info = "Activation bar width (level>1)", category = "Lifelines")
	public void setSubLifelineWidth(int subLifelineWidth);

	@Adjustable(info = "Thread 0", category = "Thread colours")
	public void setTc0(Color tc0);

	@Adjustable(info = "Thread 1", depends = "threaded=true", category = "Thread colours")
	public void setTc1(Color tc1);

	@Adjustable(info = "Thread 2", depends = "threaded=true", category = "Thread colours")
	public void setTc2(Color tc2);

	@Adjustable(info = "Thread 3", depends = "threaded=true", category = "Thread colours")
	public void setTc3(Color tc3);

	@Adjustable(info = "Thread 4", depends = "threaded=true", category = "Thread colours")
	public void setTc4(Color tc4);

	@Adjustable(info = "Thread 5", depends = "threaded=true", category = "Thread colours")
	public void setTc5(Color tc5);

	@Adjustable(info = "Thread 6", depends = "threaded=true", category = "Thread colours")
	public void setTc6(Color tc6);

	@Adjustable(info = "Thread 7", depends = "threaded=true", category = "Thread colours")
	public void setTc7(Color tc7);

	@Adjustable(info = "Thread 8", depends = "threaded=true", category = "Thread colours")
	public void setTc8(Color tc8);

	@Adjustable(info = "Thread 9", depends = "threaded=true", category = "Thread colours")
	public void setTc9(Color tc9);

	@Adjustable(dflt = 0, min = 0, max = 1, editable = true, info = "Enable multithreading", category = "Threads")
	public void setThreaded(boolean threaded);

	@Adjustable(depends = "threaded=true", dflt = 0, min = 0, max = 1, editable = true, info = "Show thread numbers", category = "Threads")
	public void setThreadNumbersVisible(boolean threadNumbersVisible);

}
