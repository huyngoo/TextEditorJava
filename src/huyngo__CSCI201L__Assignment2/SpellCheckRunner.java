package huyngo__CSCI201L__Assignment2;

import java.awt.BorderLayout;  
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import huyngo_CSCI201L_Assignment1_tho.Trie;
import huyngo_CSCI201L_Assignment1_tho.WordCorrectionCompletion;
import huyngo_CSCI201L_Assignment1_tho.WordList;

public class SpellCheckRunner {
	private JPanel newTabPanel,buttonPanel,rightPanel;
	private JButton addButton, ignoreButton, changeButton,closeButton;
	private JLabel spelling_colon,spelling_colon_word;
	private JComboBox<String> spellCheckBox;
	private JTabbedPane tabs;
	private JScrollPane newScrollPane;
	private WordList wl;
	private Trie my_trie;
	private Hashtable<Character,String> keys;
	private String curr_pane_text, curr_pane_text_with_formatting,curr_error_string, full_error_string;
	private Window mainWin;
	private configuration configureFiles;
	private Scanner sc;
	public SpellCheckRunner(JTabbedPane tabs,Trie my_trie, WordList wl, Window main,configuration configureFiles, Hashtable<Character,String> keys){
		this.tabs = tabs;
		this.wl = wl;
		this.my_trie=my_trie;
		this.mainWin=main;
		this.configureFiles=configureFiles;
		this.keys = keys;
		instantiateComponents();
	}
	private void instantiateComponents(){
		newTabPanel = new JPanel(new BorderLayout());
		tabs.getSelectedComponent().setName("Spell Check");
		wl.read_wl(configureFiles.getWLFile(), my_trie);
		tabs.getSelectedComponent();
		newScrollPane= new JScrollPane(((JScrollPane)tabs.getSelectedComponent()).getViewport().getView(),ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		curr_pane_text_with_formatting = ((JTextArea)(newScrollPane.getViewport().getView())).getText();
		curr_pane_text = ((JTextArea)(newScrollPane.getViewport().getView())).getText().toLowerCase();
		((JTextArea)(newScrollPane.getViewport().getView())).setEditable(false);
		sc = new Scanner(curr_pane_text_with_formatting);
		if(!findNextError()){
			JOptionPane.showMessageDialog(null, "No misspelled words found, good job!");
			mainWin.closeSpellCheckConfigure(((JTextArea)(newScrollPane.getViewport().getView())), tabs.getTitleAt(tabs.getSelectedIndex()));
			return;
		}
		newTabPanel.add(newScrollPane);
		setupButtons();
		setRightPanel();
		newTabPanel.add(rightPanel,BorderLayout.EAST);
		tabs.setComponentAt(tabs.getSelectedIndex(), newTabPanel);
		
		addActionListeners();
	}
	private boolean findNextError(){
		while(sc.hasNext()){
			String original = sc.next();
			String with_formatting = original.toLowerCase();
			String without_formatting = "";
			for(int i=0;i<with_formatting.length();i++){
				if(Character.isLetter(with_formatting.charAt(i))){
					without_formatting+=with_formatting.charAt(i);
				}
			}
			if(!my_trie.isWord(without_formatting) && !without_formatting.isEmpty()){
				curr_error_string = without_formatting;
				full_error_string = original;
				return true;
			}
		}
		sc.close();
		return false;
	}
	
	public String getTextArea(){
		return curr_pane_text_with_formatting;
	}
	
	private void highlightError(int start, int end, Highlighter highlighter){
		try{
			highlighter.addHighlight(start, end, DefaultHighlighter.DefaultPainter);
		}catch (BadLocationException ble){
			ble.printStackTrace();
		}
	}
	private void setRightPanel(){
		rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(buttonPanel,BorderLayout.NORTH);
		closeButton = new JButton("Close");
		rightPanel.add(closeButton,BorderLayout.SOUTH);
	}
	private void setupButtons(){
		buttonPanel = new JPanel();
		GridLayout buttonPanelLayout = new GridLayout(3,2,0,5);
		buttonPanel.setLayout(buttonPanelLayout);
		spellCheckBox = new JComboBox<String>(getSuggestions());
		spelling_colon = new JLabel("Spelling: ");
		spelling_colon_word = new JLabel(curr_error_string);
		int start = curr_pane_text_with_formatting.indexOf(full_error_string);
		if(start == -1) start = 0;
		highlightError(start,start+full_error_string.length(),((JTextArea)(newScrollPane.getViewport().getView())).getHighlighter());
		((JTextArea)(newScrollPane.getViewport().getView())).select(start, start+full_error_string.length());
		buttonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Spell Check"));
		addButton = new JButton("Add");
		ignoreButton = new JButton("Ignore");
		changeButton = new JButton("Change");
		if(spellCheckBox.getItemCount()==0) changeButton.setEnabled(false);
		else changeButton.setEnabled(true);
		buttonPanel.add(spelling_colon);
		buttonPanel.add(spelling_colon_word);
		buttonPanel.add(ignoreButton);
		buttonPanel.add(addButton);
		buttonPanel.add(spellCheckBox);
		buttonPanel.add(changeButton);
	}
	private String [] getSuggestions(){
		String [] arr;
		Set<String> suggestions=WordCorrectionCompletion.wordCorrectionCompletion("", curr_error_string, my_trie, keys, curr_error_string.charAt(0),0);
		List<String> to_list = new ArrayList<String>();
		HashMap<Integer, ArrayList<String>> len_to_str = new HashMap<Integer, ArrayList<String>>();
		for(String s: suggestions){
			int char_diff = s.length()-curr_error_string.length();
			for(int k=0;k<curr_error_string.length();k++){
				if(s.charAt(k)!=curr_error_string.charAt(k)) char_diff++;
			}
			if(!len_to_str.containsKey(char_diff)) len_to_str.put(char_diff,new ArrayList<String>());
			len_to_str.get(char_diff).add(s);
		}
		Iterator it = len_to_str.entrySet().iterator();
		int count=0;
		while(it.hasNext() && count <10){
			Map.Entry<Integer, List<String>> pair = (Map.Entry<Integer,List<String>>)it.next();
			for(int j=0;j<pair.getValue().size();j++,count++) to_list.add(pair.getValue().get(j));
		}
		arr = to_list.toArray(new String[to_list.size()]);
		return arr;
	}
	
	private void addActionListeners(){
		ignoreButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(findNextError()){
					spelling_colon_word.setText(curr_error_string);
					changeComboBox();
					((JTextArea)(newScrollPane.getViewport().getView())).getHighlighter().removeAllHighlights();
					int start = curr_pane_text_with_formatting.indexOf(full_error_string,((JTextArea)(newScrollPane.getViewport().getView())).getSelectionEnd());
					if(start == -1) start = curr_pane_text_with_formatting.indexOf(full_error_string,((JTextArea)(newScrollPane.getViewport().getView())).getSelectionStart());
					((JTextArea)(newScrollPane.getViewport().getView())).select(start, start+full_error_string.length());
					highlightError(start,start+full_error_string.length(),((JTextArea)(newScrollPane.getViewport().getView())).getHighlighter());
				}else{
					((JTextArea)(newScrollPane.getViewport().getView())).getHighlighter().removeAllHighlights();
					((JTextArea)(newScrollPane.getViewport().getView())).select(0, 0);
					mainWin.closeSpellCheckConfigure(((JTextArea)(newScrollPane.getViewport().getView())),tabs.getTitleAt(tabs.getSelectedIndex()));
				}
			}
		});
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				((JTextArea)(newScrollPane.getViewport().getView())).getHighlighter().removeAllHighlights();
				((JTextArea)(newScrollPane.getViewport().getView())).select(0, 0);
				mainWin.closeSpellCheckConfigure(((JTextArea)(newScrollPane.getViewport().getView())),tabs.getTitleAt(tabs.getSelectedIndex()));
			}
		});
		changeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				int start = ((JTextArea)(newScrollPane.getViewport().getView())).getSelectionStart();
				if(start == -1) start = ((JTextArea)(newScrollPane.getViewport().getView())).getSelectionEnd();
				try {
					((JTextArea)(newScrollPane.getViewport().getView())).replaceRange((String)spellCheckBox.getSelectedItem(), start,start+full_error_string.length());
				} catch (IllegalArgumentException iae){
					((JTextArea)(newScrollPane.getViewport().getView())).replaceRange((String)spellCheckBox.getSelectedItem(), start,start+((String)spellCheckBox.getSelectedItem()).length());
				} 
				curr_pane_text_with_formatting = ((JTextArea)(newScrollPane.getViewport().getView())).getText();
				curr_pane_text = ((JTextArea)(newScrollPane.getViewport().getView())).getText().toLowerCase();
				if(findNextError()){
					spelling_colon_word.setText(curr_error_string);
					changeComboBox();
					((JTextArea)(newScrollPane.getViewport().getView())).getHighlighter().removeAllHighlights();
					start = curr_pane_text_with_formatting.indexOf(full_error_string,((JTextArea)(newScrollPane.getViewport().getView())).getSelectionEnd());
					if(start == -1) start = ((JTextArea)(newScrollPane.getViewport().getView())).getSelectionStart();					
					((JTextArea)(newScrollPane.getViewport().getView())).select(start, start+full_error_string.length());
					highlightError(start,start+full_error_string.length(),((JTextArea)(newScrollPane.getViewport().getView())).getHighlighter());
				}else {
					((JTextArea)(newScrollPane.getViewport().getView())).getHighlighter().removeAllHighlights();
					((JTextArea)(newScrollPane.getViewport().getView())).select(0,0);
					mainWin.closeSpellCheckConfigure(((JTextArea)(newScrollPane.getViewport().getView())),tabs.getTitleAt(tabs.getSelectedIndex()));
				}
			}
		});
		addButton.addActionListener(new ActionListener(){ //finish this later
			public void actionPerformed(ActionEvent ae){
				my_trie.addWord(curr_error_string);
				addWordToWLFile(curr_error_string);
				if(findNextError()){
					spelling_colon_word.setText(curr_error_string);
					changeComboBox();
					((JTextArea)(newScrollPane.getViewport().getView())).getHighlighter().removeAllHighlights();
					int start = curr_pane_text_with_formatting.indexOf(full_error_string,((JTextArea)(newScrollPane.getViewport().getView())).getSelectionEnd());
					if(start == -1) start = curr_pane_text_with_formatting.indexOf(full_error_string,((JTextArea)(newScrollPane.getViewport().getView())).getSelectionStart());
					((JTextArea)(newScrollPane.getViewport().getView())).select(start,start+full_error_string.length());
					highlightError(start,start+full_error_string.length(),((JTextArea)(newScrollPane.getViewport().getView())).getHighlighter());
				}else {
					((JTextArea)(newScrollPane.getViewport().getView())).getHighlighter().removeAllHighlights();
					((JTextArea)(newScrollPane.getViewport().getView())).select(0, 0);
					mainWin.closeSpellCheckConfigure(((JTextArea)(newScrollPane.getViewport().getView())),tabs.getTitleAt(tabs.getSelectedIndex()));
				}
			}
		});
	}
	
	private void addWordToWLFile(String word){
		FileWriter fw =null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(configureFiles.getWLFile(),true);
			pw = new PrintWriter(fw);
			pw.print('\n' + word);
			fw.close();
			pw.close();
		} catch (IOException ioe){
			ioe.printStackTrace();
		} 
	}
	
	private void changeComboBox(){
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(getSuggestions());
		spellCheckBox.setModel(model);
		if(spellCheckBox.getItemCount()==0) changeButton.setEnabled(false);
		else changeButton.setEnabled(true);
	}
}
