package huyngo__CSCI201L__Assignment2;

import java.awt.BorderLayout; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import huyngo_CSCI201L_Assignment1_tho.Trie;
import huyngo_CSCI201L_Assignment1_tho.WordCorrectionCompletion;
import huyngo_CSCI201L_Assignment1_tho.WordList;

public class configuration {
	private String wl_file,kb_file;
	private JTabbedPane tabs;
	private JPanel configurePanel,buttonPanel,closePanel, newTabPanel;
	private JButton selectWlButton,selectKbButton,closeButton;
	private JScrollPane scrollPane;
	private JLabel wordlist,keyboard;
	private JFileChooser fileChooser;
	private Window mainWin;
	private String text_in_pane;
	public configuration(String wl_file, String kb_file, Trie my_trie, WordList wl){
		this.wl_file=wl_file;
		this.kb_file=kb_file;
	}
	private void instantiateComponents(){
		newTabPanel = new JPanel(new BorderLayout());
		newTabPanel.setName("Configure");
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
		wordlist = new JLabel(getWLFile().substring(wl_file.lastIndexOf('\\')+1, wl_file.length()));
		keyboard = new JLabel(getKBFile().substring(kb_file.lastIndexOf('\\')+1, kb_file.length()));
		setupButtonPanel();		
		closePanel = new JPanel(new BorderLayout());
		setupClosePanel();
		configurePanel = new JPanel(new BorderLayout());
		setupConfigurePanel();	
		scrollPane = new JScrollPane(((JScrollPane)tabs.getSelectedComponent()).getViewport().getView(),ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		newTabPanel.add(scrollPane);
		newTabPanel.add(configurePanel,BorderLayout.EAST);
		tabs.setComponentAt(tabs.getSelectedIndex(), newTabPanel);
		
		mainWin.addUndoToTab();
		
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new java.io.File("."));
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		addActionListeners();
	}
	private void setupClosePanel(){		
		closeButton = new JButton("Close");
		closePanel.add(closeButton,BorderLayout.WEST);
	}
	private void setupConfigurePanel(){
		configurePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Configure"));
		configurePanel.add(buttonPanel,BorderLayout.NORTH);
		configurePanel.add(closePanel,BorderLayout.SOUTH);
	}
	private void setupButtonPanel(){
		selectWlButton = new JButton("Select WordList...");
		selectKbButton = new JButton("Select Keyboard...");
		buttonPanel.add(wordlist);
		buttonPanel.add(selectWlButton);
		buttonPanel.add(new JLabel("\n"));
		buttonPanel.add(keyboard);
		buttonPanel.add(selectKbButton);
	}
	public String getWLFile(){
		return wl_file;
	}
	public String getKBFile(){
		return kb_file;
	}
	public void setWLFile(String file){
		wl_file = file;
	}
	public void setKBFile(String file){
		kb_file = file;
	}
	public String getTextArea(){
		return text_in_pane;
	}
	public void addConfigMenu(JTabbedPane tabs,Window mainWin){
		this.tabs=tabs;
		this.mainWin = mainWin;
		text_in_pane = ((JTextArea)((JScrollPane)tabs.getSelectedComponent()).getViewport().getView()).getText();
		instantiateComponents();
	}
	private void addActionListeners(){
		selectWlButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				openWLFile();
			}
		});
		selectKbButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				openKBFile();
			}
		});
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				mainWin.closeSpellCheckConfigure(((JTextArea)(scrollPane.getViewport().getView())),tabs.getTitleAt(tabs.getSelectedIndex()));
			}
		});
	}
	private void openWLFile(){
		JButton open = new JButton();
		fileChooser.setDialogTitle("Open File...");
		FileNameExtensionFilter wl_filter = new FileNameExtensionFilter("WORDLIST FILES", "wl","wordlist");
		fileChooser.setFileFilter(wl_filter);
		if(fileChooser.showOpenDialog(open) == JFileChooser.APPROVE_OPTION){
			if(fileChooser.getSelectedFile().exists() && WordCorrectionCompletion.getExt(fileChooser.getSelectedFile().getPath()).equals("wl") && fileChooser.getSelectedFile().isFile()){
				setWLFile(fileChooser.getSelectedFile().getPath());
				wordlist.setText(fileChooser.getSelectedFile().getPath().substring(getWLFile().lastIndexOf('\\')+1, getWLFile().length()));
				mainWin.fixTrie(fileChooser.getSelectedFile().getPath());
			}
			else JOptionPane.showMessageDialog(fileChooser,"Invalid file, please retry entering a file with a .wl extension","Try again....",JOptionPane.WARNING_MESSAGE);
		}
	}
	private void openKBFile(){
		JButton open = new JButton();
		fileChooser.setDialogTitle("Open File...");
		FileNameExtensionFilter kb_filter = new FileNameExtensionFilter("KEYBOARD FILES", "kb","keyboard");
		fileChooser.setFileFilter(kb_filter);
		if(fileChooser.showOpenDialog(open) == JFileChooser.APPROVE_OPTION){
			if(fileChooser.getSelectedFile().exists() && WordCorrectionCompletion.getExt(fileChooser.getSelectedFile().getPath()).equals("kb") && fileChooser.getSelectedFile().isFile()){
				setKBFile(fileChooser.getSelectedFile().getPath());
				keyboard.setText(fileChooser.getSelectedFile().getPath().substring(getKBFile().lastIndexOf('\\')+1, getKBFile().length()));
				mainWin.changeKBFile(getKBFile());
			}else JOptionPane.showMessageDialog(fileChooser,"Invalid file, please retry entering a file with a .kb extension","Try again..",JOptionPane.WARNING_MESSAGE);
		}
	}
}
