// Copyright (c) 2006 - 2011, Markus Strauch.
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// * Redistributions of source code must retain the above copyright notice, 
// this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
// this list of conditions and the following disclaimer in the documentation 
// and/or other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
// THE POSSIBILITY OF SUCH DAMAGE.

package net.sf.sdedit.config;

import java.awt.Color;
import java.awt.Font;

import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.DataObject;

public interface Configuration extends DataObject
{
    public int getActorWidth();

    public int getArrowSize();

    public int getDestructorWidth();

    public Font getFont();

    public int getFragmentMargin();

    public int getFragmentPadding();

    public int getFragmentTextPadding();

    public int getGlue();

    public int getHeadHeight();

    public int getHeadLabelPadding();
    
    public int getHeadWidth();

    public int getInitialSpace();

    public int getLeftMargin();
    
    public boolean isLineWrap();

    public int getLowerMargin();

    public int getMainLifelineWidth();

    public int getMessageLabelSpace();

    public int getMessagePadding();

    public int getNoteMargin();

    public int getNotePadding();

    public int getRightMargin();

    public int getSelfMessageHorizontalSpace();

    public int getSeparatorBottomMargin();

    public int getSeparatorTopMargin();

    public int getSpaceBeforeActivation();

    public int getSpaceBeforeAnswerToSelf();

    public int getSpaceBeforeConstruction();
    
    public int getSpaceBeforeSelfMessage();

    public int getSubLifelineWidth();

    public int getUpperMargin();
    
    public Color getArrowColor ();
    
    public Color getLabeledBoxBgColor ();
    
    public Color getFragmentEdgeColor ();
    
    public Color getFragmentLabelBgColor();
    
    public Color getNoteBgColor();
    

    
    public Color getTc0 ();
    
    public Color getTc1 ();
    
    public Color getTc2 ();
    
    public Color getTc3 ();
    
    public Color getTc4 ();
    
    public Color getTc5 ();
    
    public Color getTc6 ();
    
    public Color getTc7 ();
    
    public Color getTc8 ();
    
    public Color getTc9 ();
    
    public boolean isColorizeThreads();

    public boolean isOpaqueMessageText();

    public boolean isThreaded();

    public boolean isThreadNumbersVisible();
    
    public boolean isReturnArrowVisible();

    public boolean isVerticallySplit();
    
    public boolean isExplicitReturns();
    
    public int getArrowThickness ();
    
    public int getActivationBarBorderThickness();
    
    public int getLifelineThickness ();
    
    public int getNoteBorderThickness ();
    
    public int getFragmentBorderThickness ();
    
    public boolean isSlackMode ();
    
    public boolean isShouldShadowParticipants();

    @Adjustable(dflt=25,min=20,max=100,info="Actor width",category="Lifelines")
    public void setActorWidth(int actorWidth);

    @Adjustable(dflt=6,min=2,max=100,info="Arrowhead size",category="Misc")
    public void setArrowSize(int arrowSize);

    @Adjustable(depends="threaded=true,slackMode=false",info="Colourize threads",category="Threads")
    public void setColorizeThreads(boolean colorizeThreads);

    @Adjustable(dflt=30,min=5,max=100,info="Destructor cross width",category="Misc")
    public void setDestructorWidth(int destructorWidth);

    @Adjustable(category = "Misc", info = "Diagram font")
    public void setFont(Font font);

    @Adjustable(dflt=8,min=0,max=100,editable=true,info="Fragment margin",category="Fragments")
    public void setFragmentMargin(int fragmentMargin);

    @Adjustable(dflt=10,min=1,max=100,editable=true,info="Fragment padding",category="Fragments")
    public void setFragmentPadding(int commentPadding);

    @Adjustable(dflt=3,min=1,max=100,editable=true,info="Fragment label padding",category="Fragments")
    public void setFragmentTextPadding(int commentTextPadding);

    @Adjustable(dflt=10,min=0,max=999,info="Glue",category="Misc")
    public void setGlue(int glue);

    @Adjustable(dflt=35,min=20,max=100,info="Head height",category="Lifelines")
    public void setHeadHeight(int headHeight);

    @Adjustable(dflt=5,min=1,max=100,editable=true,info="Head label padding",category="Lifelines")
    public void setHeadLabelPadding(int headLabelPadding);

    @Adjustable(dflt=100,min=50,max=300,step=1,info="Head width",category="Lifelines")
    public void setHeadWidth(int headWidth);

    @Adjustable(dflt=10,min=8,max=100,editable=true,info="Space below lifeline head",category="Vertical spaces")
    public void setInitialSpace(int initialSpace);

    @Adjustable(dflt=5,min=1,max=999,info="Left margin",category="Margins")
    public void setLeftMargin(int leftMargin);
    
    @Adjustable(info="Wrap lines",category="Misc")
    public void setLineWrap(boolean lineWrap);

    @Adjustable(dflt=5,min=1,max=100,info="Bottom margin",category="Margins")
    public void setLowerMargin(int lowerMargin);

    @Adjustable(dflt=8,min=2,max=50,info="Activation bar width (1st level)",category="Lifelines")
    public void setMainLifelineWidth(int width);

    @Adjustable(dflt=3,min=1,max=100,editable=true,info="Space below message label",category="Vertical spaces")
    public void setMessageLabelSpace(int messageLabelSpace);

    @Adjustable(dflt=6,min=1,max=100,info="Message padding",category="Messages")
    public void setMessagePadding(int messagePadding);
    
    @Adjustable(info="Show dashed lines for return messages with no text",category="Messages")
    public void setReturnArrowVisible(boolean visible);

    @Adjustable(dflt=6,min=1,max=20,info="Note box margin",category="Notes")
    public void setNoteMargin(int noteMargin);

    @Adjustable(dflt=6,min=1,max=20,info="Note box padding",category="Notes")
    public void setNotePadding(int notePadding);

    @Adjustable(depends="threaded=true,colorizeThreads=true",info="Colourize message text background", category="Threads")
    public void setOpaqueMessageText(boolean opaqueMessageText);

    @Adjustable(dflt=5,min=5,max=999,editable=true,info="Right margin",category="Margins")
    public void setRightMargin(int rightMargin);

    @Adjustable(dflt=15,min=10,max=100,info="Self-messages width",category="Messages")
    public void setSelfMessageHorizontalSpace(int selfMessageHorizontalSpace);

    @Adjustable(dflt=8,min=1,max=100,info="Separator bottom margin",category="Fragments")
    public void setSeparatorBottomMargin(int beforeFragmentText);

    @Adjustable(dflt=15,min=1,max=100,info="Separator top margin",category="Fragments")
    public void setSeparatorTopMargin(int beforeSeparator);

    @Adjustable(dflt=2,min=0,max=100,info="Space before activation",category="Vertical spaces")
    public void setSpaceBeforeActivation(int spaceBeforeActivation);

    @Adjustable(dflt=10,min=10,max=100,editable=true,info="Space before answer to self",category="Vertical spaces")
    public void setSpaceBeforeAnswerToSelf(int spaceBeforeAnswerToSelf);

    @Adjustable(dflt=6,min=1,max=100,info="Space before constructor",category="Vertical spaces")
    public void setSpaceBeforeConstruction(int spaceBeforeConstruction);

    @Adjustable(dflt=7,min=3,max=100,info="Space before message to self",category="Vertical spaces")
    public void setSpaceBeforeSelfMessage(int spaceBeforeSelfMessage);

   
    @Adjustable(dflt=6,min=2,max=100,info="Activation bar width (level>1)",category="Lifelines")
    public void setSubLifelineWidth(int subLifelineWidth);
    
    @Adjustable(info="Arrow colour",category="Colours")
    public void setArrowColor (Color c);
    
    @Adjustable(info="Head background colour",category="Colours")
    public void setLabeledBoxBgColor (Color c);
    
    @Adjustable(info="Fragment edge colour",category="Colours")
    public void setFragmentEdgeColor (Color c);
    
    @Adjustable(info="Fragment label background colour",category="Colours")
    public void setFragmentLabelBgColor(Color c);
    
    @Adjustable(info="Note background colour",category="Colours")
    public void setNoteBgColor(Color c);
    
    @Adjustable(info="Thread 0",category="Thread colours")
    public void setTc0 (Color tc0);
    
    @Adjustable(info="Thread 1",depends="slackMode=false,threaded=true",category="Thread colours")
    public void setTc1 (Color tc1);
    
    @Adjustable(info="Thread 2",depends="slackMode=false,threaded=true",category="Thread colours")
    public void setTc2 (Color tc2);
    
    @Adjustable(info="Thread 3",depends="slackMode=false,threaded=true",category="Thread colours")
    public void setTc3 (Color tc3);
    
    @Adjustable(info="Thread 4",depends="slackMode=false,threaded=true",category="Thread colours")
    public void setTc4 (Color tc4);
    
    @Adjustable(info="Thread 5",depends="slackMode=false,threaded=true",category="Thread colours")
    public void setTc5 (Color tc5);
    
    @Adjustable(info="Thread 6",depends="slackMode=false,threaded=true",category="Thread colours")
    public void setTc6 (Color tc6);
    
    @Adjustable(info="Thread 7",depends="slackMode=false,threaded=true",category="Thread colours")
    public void setTc7 (Color tc7);
    
    @Adjustable(info="Thread 8",depends="slackMode=false,threaded=true",category="Thread colours")
    public void setTc8 (Color tc8);
    
    @Adjustable(info="Thread 9",depends="slackMode=false,threaded=true",category="Thread colours")
    public void setTc9 (Color tc9);
    
    @Adjustable(dflt=0,min=0,max=1,editable=true,info="Enable multithreading",category="Threads",depends="slackMode=false")
    public void setThreaded(boolean threaded);

    @Adjustable(depends="threaded=true",dflt=0,min=0,max=1,editable=true,info="Show thread numbers", category="Threads")
    public void setThreadNumbersVisible(boolean threadNumbersVisible);
	
    @Adjustable(dflt=5,min=1,max=100,info="Top margin",category="Margins")
    public void setUpperMargin(int upperMargin);

    @Adjustable(editable=false,info="Vertical split",category="Misc")
    public void setVerticallySplit(boolean verticallySplit);
    
    @Adjustable(editable=true,info="Require explicit returns",category="Misc")
    public void setExplicitReturns(boolean explicitReturns);
    
    @Adjustable(editable=true,info="Arrow thickness", category="Line thickness", min=1)
    public void setArrowThickness (int arrowThickness);
    
    @Adjustable(editable=true,info="Activation bar border thickness", category="Line thickness",min=1)
    public void setActivationBarBorderThickness(int activationBarBorderThickness);
    
    @Adjustable(editable=true,info="(Inactive) Lifeline thickness", category="Line thickness",min=1)
    public void setLifelineThickness (int lifelineThickness);
    
    @Adjustable(editable=true,info="Note border thickness", category="Line thickness",min=1)
    public void setNoteBorderThickness(int noteBorderThickness);
    
    @Adjustable(editable=true,info="Fragment border thickness", category="Line thickness",min=1)
    public void setFragmentBorderThickness (int fragmentBorderThickness);
    
    @Adjustable(editable=true, category = "Threads", info = "Slack mode")
    public void setSlackMode (boolean slackMode);
    
    @Adjustable(info="Put shadows on participants", category="Lifelines")
    public void setShouldShadowParticipants(boolean shouldShadowParticipants);

}
//{{core}}
