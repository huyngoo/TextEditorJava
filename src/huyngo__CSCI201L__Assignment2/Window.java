package huyngo__CSCI201L__Assignment2;


import java.awt.BorderLayout;  
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import huyngo_CSCI201L_Assignment1_tho.Trie;
import huyngo_CSCI201L_Assignment1_tho.WordCorrectionCompletion;
import huyngo_CSCI201L_Assignment1_tho.WordList;

public class Window extends JFrame{
	public static final long serialVersionUID = 1;
	private JMenuBar menuBar;
	private JMenu file_menu, edit_menu, spellcheck_menu;
	private JMenuItem new_file_item, open_file_item, save_file_item, close_file_item;
	private JMenuItem undo_item, redo_item, cut_item, copy_item, paste_item,select_all_item;
	private JMenuItem run_item, configure_item;
	private JTabbedPane tabs;
	private JFileChooser file_chooser;
	private String cut_copy_text;
	private JPanel allPanel;
	private WordList wl;
	private Trie my_trie;
	private SpellCheckRunner spellCheckRunner;
	private configuration configureFiles;
	private InputMap im;
	private ActionMap am;
	private ArrayList<UndoManager> undoManagers;
	private Hashtable<Character,String> keys;
	public Window(){
		super("Trojan Word Editor");
		setupGUI();
		if(System.getProperty("os.name").contains("Windows")){
			setupEditMenuWindows();
			setupFileMenuWindows();
		}
		else{
			setupEditMenuMac();
			setupFileMenuMac();
		}
		setupSpellCheckMenu();
		instantiateComponents();
		addMenuListeners();
	}
	private void instantiateComponents(){
		allPanel = new JPanel(new BorderLayout());
		menuBar = new JMenuBar();
		menuBar.add(file_menu);
		menuBar.add(edit_menu);
		menuBar.add(spellcheck_menu);
		edit_menu.setVisible(false);
		spellcheck_menu.setVisible(false);
		tabs = new JTabbedPane();
		allPanel.add(tabs);
		file_chooser = new JFileChooser();
		file_chooser.setCurrentDirectory(new java.io.File("."));
		FileNameExtensionFilter txt_filter = new FileNameExtensionFilter("TEXT FILES", "txt","text");
		file_chooser.setFileFilter(txt_filter);
		file_chooser.setAcceptAllFileFilterUsed(false);
		cut_copy_text = "";
		setJMenuBar(menuBar);
		wl = new WordList();
		my_trie = new Trie();
		add(allPanel);
		undoManagers = new ArrayList<UndoManager>();
		instantiateDefaultConfigure();
	}

	private void instantiateDefaultConfigure(){
		if(new File("wordlist.wl").exists() && new File("qwerty-us.kb").exists()){
			configureFiles = new configuration("wordlist.wl","qwerty-us.kb",my_trie,wl);
			wl.read_wl(configureFiles.getWLFile(), my_trie);
			keys= WordCorrectionCompletion.read_kb_to_hash(configureFiles.getKBFile());
		}else{
			configureFiles = new configuration("","",my_trie,wl);
			newTab();
			runConfig();
//			wl.read_wl(configureFiles.getWLFile(), my_trie);
//			keys= WordCorrectionCompletion.read_kb_to_hash(configureFiles.getKBFile());
		}
		
	}
	
	private void setupSpellCheckMenu(){
		spellcheck_menu = new JMenu("SpellCheck");
		spellcheck_menu.setMnemonic(KeyEvent.VK_S);
		run_item = new JMenuItem("Run",KeyEvent.VK_F7);
		run_item.setMnemonic(KeyEvent.VK_R);
		run_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7,0));
		spellcheck_menu.add(run_item);
		configure_item = new JMenuItem("Configure");
		configure_item.setMnemonic(KeyEvent.VK_C);
		spellcheck_menu.add(configure_item);
	}
	
	private void setupEditMenuMac(){
		edit_menu = new JMenu("Edit");
		undo_item = new JMenuItem("Undo", KeyEvent.VK_Z);
		undo_item.setMnemonic(KeyEvent.VK_U);
		undo_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,KeyEvent.META_DOWN_MASK));
		redo_item = new JMenuItem("Redo",KeyEvent.VK_Y);
		redo_item.setMnemonic(KeyEvent.VK_R);
		redo_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,KeyEvent.META_DOWN_MASK));
		edit_menu.add(undo_item);
		edit_menu.add(redo_item);
		edit_menu.addSeparator();
		cut_item = new JMenuItem("Cut",KeyEvent.VK_X);
		cut_item.setMnemonic(KeyEvent.VK_C);
		cut_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,KeyEvent.META_DOWN_MASK));
		copy_item = new JMenuItem("Copy",KeyEvent.VK_C);
		copy_item.setMnemonic(KeyEvent.VK_C);
		copy_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,KeyEvent.META_DOWN_MASK));
		paste_item = new JMenuItem("Paste",KeyEvent.VK_V);
		paste_item.setMnemonic(KeyEvent.VK_P);
		paste_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,KeyEvent.META_DOWN_MASK));
		edit_menu.add(cut_item);
		edit_menu.add(copy_item);
		edit_menu.add(paste_item);
		edit_menu.addSeparator();
		select_all_item = new JMenuItem("Select All");
		select_all_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,KeyEvent.META_DOWN_MASK));
		edit_menu.add(select_all_item);
	}
	
	private void cut(){
		cut_copy_text = ((JTextArea)((JScrollPane)tabs.getSelectedComponent()).getViewport().getView()).getSelectedText();
		((JTextArea)((JScrollPane)tabs.getSelectedComponent()).getViewport().getView()).replaceSelection("");
		StringSelection stringSelection = new StringSelection(cut_copy_text);
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		c.setContents(stringSelection, null);		
	}
	private void copy(){
		cut_copy_text = ((JTextArea)((JScrollPane)tabs.getSelectedComponent()).getViewport().getView()).getSelectedText();
		StringSelection stringSelection = new StringSelection(cut_copy_text);
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		c.setContents(stringSelection, null);
	}
	private void paste(){
		String to_paste = "";
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = c.getContents(null);
		boolean hasText = (contents!=null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if(hasText){
			try{
				to_paste = (String)contents.getTransferData(DataFlavor.stringFlavor);
				int pos = ((JTextArea)((JScrollPane)tabs.getSelectedComponent()).getViewport().getView()).getCaretPosition();
				try{
					((JTextArea)((JScrollPane)tabs.getSelectedComponent()).getViewport().getView()).getDocument().insertString(pos, to_paste, null);
				}catch (BadLocationException ble){
					ble.printStackTrace();
				}
			}catch (UnsupportedFlavorException | IOException e){
				e.printStackTrace();
			}
		}
	}
	private void setupEditMenuWindows(){
		edit_menu = new JMenu("Edit");
		edit_menu.setMnemonic(KeyEvent.VK_E);
		undo_item = new JMenuItem("Undo", KeyEvent.VK_Z);
		undo_item.setMnemonic(KeyEvent.VK_U);
		undo_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,ActionEvent.CTRL_MASK));
		redo_item = new JMenuItem("Redo",KeyEvent.VK_Y);
		redo_item.setMnemonic(KeyEvent.VK_R);
		redo_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,ActionEvent.CTRL_MASK));
		edit_menu.add(undo_item);
		edit_menu.add(redo_item);
		edit_menu.addSeparator();
		cut_item = new JMenuItem("Cut",KeyEvent.VK_X);
		cut_item.setMnemonic(KeyEvent.VK_C);
		cut_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK));
		copy_item = new JMenuItem("Copy",KeyEvent.VK_C);
		copy_item.setMnemonic(KeyEvent.VK_C);
		copy_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK));
		paste_item = new JMenuItem("Paste",KeyEvent.VK_V);
		paste_item.setMnemonic(KeyEvent.VK_P);
		paste_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK));
		edit_menu.add(cut_item);
		edit_menu.add(copy_item);
		edit_menu.add(paste_item);
		edit_menu.addSeparator();
		select_all_item = new JMenuItem("Select All");
		select_all_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
		edit_menu.add(select_all_item);
	}
	private void setupFileMenuWindows(){
		file_menu = new JMenu("File");
		file_menu.setMnemonic(KeyEvent.VK_F);
		new_file_item = new JMenuItem("New",KeyEvent.VK_N);
		new_file_item.setMnemonic(KeyEvent.VK_N);
		new_file_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));
		file_menu.add(new_file_item);
		open_file_item = new JMenuItem("Open",KeyEvent.VK_O);
		open_file_item.setMnemonic(KeyEvent.VK_O);
		open_file_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
		file_menu.add(open_file_item);
		save_file_item = new JMenuItem("Save",KeyEvent.VK_S);
		save_file_item.setMnemonic(KeyEvent.VK_S);
		save_file_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
		file_menu.add(save_file_item);
		close_file_item = new JMenuItem("Close");
		close_file_item.setMnemonic(KeyEvent.VK_C);
		file_menu.add(close_file_item);		
	}
	private void setupFileMenuMac(){
		file_menu = new JMenu("File");
		file_menu.setMnemonic(KeyEvent.VK_F);
		new_file_item = new JMenuItem("New",KeyEvent.VK_N);
		new_file_item.setMnemonic(KeyEvent.VK_N);
		new_file_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,KeyEvent.META_DOWN_MASK));
		file_menu.add(new_file_item);
		open_file_item = new JMenuItem("Open",KeyEvent.VK_O);
		open_file_item.setMnemonic(KeyEvent.VK_O);
		open_file_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,KeyEvent.META_DOWN_MASK));
		file_menu.add(open_file_item);
		save_file_item = new JMenuItem("Save",KeyEvent.VK_S);
		save_file_item.setMnemonic(KeyEvent.VK_S);
		save_file_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.META_DOWN_MASK));
		file_menu.add(save_file_item);
		close_file_item = new JMenuItem("Close");
		close_file_item.setMnemonic(KeyEvent.VK_C);
		file_menu.add(close_file_item);		
	}
	private void addMenuListeners(){
		open_file_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				openFile();
			}
		});
		new_file_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				newTab();
			}
		});
		save_file_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				if(tabs.getTabCount()==0) System.out.println("Can't save without having a file open!");
				else saveFile();
			}
		});
		close_file_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				if(tabs.getTabCount()==0) System.exit(0);
				else{
					if(tabs.getSelectedComponent() instanceof JPanel) closeIfConfigureOrSpellCheckOpen();
					tabs.remove(tabs.getSelectedIndex());
					if(tabs.getTabCount()==0){
						edit_menu.setVisible(false);
						spellcheck_menu.setVisible(false);
					}
				}
			}
		});
		cut_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				cut();
			}
		});
		copy_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				copy();
			}
		});
		paste_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				paste();
			}
		});
		select_all_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				((JTextArea)((JScrollPane)tabs.getSelectedComponent()).getViewport().getView()).selectAll();
			}
		});
		run_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				closeIfConfigureOrSpellCheckOpen();
				runSpellCheck(tabs,my_trie,wl);
			}
		});
		configure_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				if(tabs.getSelectedComponent() instanceof JScrollPane) ((JTextArea)((JScrollPane)(tabs.getSelectedComponent())).getViewport().getView()).getHighlighter().removeAllHighlights();
				else closeIfConfigureOrSpellCheckOpen();
				runConfig();
			}
		});
		redo_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				try{ 
					if(undoManagers.get(tabs.getSelectedIndex()).canRedo()) undoManagers.get(tabs.getSelectedIndex()).redo();
				}catch (CannotRedoException cre){
					undoManagers.get(tabs.getSelectedIndex()).discardAllEdits();
				}
			}
		});
		undo_item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				try{
					if(undoManagers.get(tabs.getSelectedIndex()).canUndo()) undoManagers.get(tabs.getSelectedIndex()).undo();
				}catch (CannotUndoException cue){
					undoManagers.get(tabs.getSelectedIndex()).discardAllEdits();
				}
			}
		});
	}
	private void closeIfConfigureOrSpellCheckOpen(){
		if(tabs.getSelectedComponent() instanceof JPanel){
			Component [] components = ((JPanel)tabs.getSelectedComponent()).getComponents();
			for(int i=0;i<components.length;i++){
				if(components[i].getClass().getName().toString().equals("javax.swing.JScrollPane")){
					JViewport viewport =((JScrollPane)components[i]).getViewport();
					closeSpellCheckConfigure(((JTextArea)viewport.getView()),tabs.getTitleAt(tabs.getSelectedIndex()));
				}
			}
		}
	}
	
	private void runConfig(){
		configureFiles.addConfigMenu(tabs,this);
	}
	
	public void fixTrie(String newWLFile){
		my_trie = new Trie();
		wl = new WordList();
		wl.read_wl(newWLFile, my_trie);
	}
	
	private void runSpellCheck(JTabbedPane tabs,Trie my_trie, WordList wl){
		spellCheckRunner = new SpellCheckRunner(tabs,my_trie,wl,this,configureFiles,keys);
	}
	public void closeSpellCheckConfigure(JTextArea textArea, String tabName){
		JScrollPane scroll = new JScrollPane(textArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tabs.setComponentAt(tabs.getSelectedIndex(), scroll);
		tabs.setTitleAt(tabs.getSelectedIndex(), tabName);
		textArea.setEditable(true);
		undo_item.setEnabled(true);
		redo_item.setEnabled(true);
	}
	private void setupGUI(){
		setSize(500,500);
		setLocation(500,100);
		setLayout(new GridLayout(1,2));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	private void newTab(){
		JTextArea textArea = new JTextArea();
		JScrollPane scroll = new JScrollPane(textArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tabs.add(scroll);
		if(tabs.getTabCount()==1){
			edit_menu.setVisible(true);
			spellcheck_menu.setVisible(true);
		}
		tabs.setSelectedIndex(tabs.getTabCount()-1);
		tabs.setTitleAt(tabs.getSelectedIndex(), "New");
		undoManagers.add(addUndoToTab());
	}
	
	private void openFile(){
		JButton open = new JButton();
		file_chooser.setDialogTitle("Open File...");
		if(file_chooser.showOpenDialog(open) == JFileChooser.APPROVE_OPTION){
			if(WordCorrectionCompletion.getExt(file_chooser.getSelectedFile().toString()).equals("txt") && file_chooser.getSelectedFile().isFile() && file_chooser.getSelectedFile().exists()) openFileContents(file_chooser.getSelectedFile());
			else JOptionPane.showMessageDialog(null, "Invalid file format, please supply .txt file", "Try again...", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private JScrollPane getJScrollPaneFromJPanel(){
		Component [] components = ((JPanel)tabs.getSelectedComponent()).getComponents();
		JScrollPane to_return = new JScrollPane();
		for(int i=0;i<components.length;i++){
			if(components[i].getClass().getName().toString().equals("javax.swing.JScrollPane")){
				to_return = (JScrollPane)components[i];
				return (JScrollPane)components[i];
			}
		}
		return to_return;
	}
	
	private void saveFile(){
		JButton save = new JButton();
		file_chooser.setDialogTitle("Save As...");
		if(file_chooser.showSaveDialog(save)==JFileChooser.APPROVE_OPTION){
			File file = file_chooser.getSelectedFile();
			if(!WordCorrectionCompletion.getExt(file.getPath()).equals("txt")) JOptionPane.showMessageDialog(null, "File needs extension .txt, please specify", "Warning", JOptionPane.WARNING_MESSAGE);
			else if(file.isFile() && !file.isDirectory()){
				int confirmation = JOptionPane.showConfirmDialog(null,file.getName()+" already exists, do you want to replace it?", "Confirm",JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if(confirmation == JOptionPane.YES_OPTION){
					FileWriter fw = null;
					PrintWriter pw = null;
					try{
						fw = new FileWriter(file);
						pw = new PrintWriter(fw);
						pw.print(getTextAreaFromTab().getText());
//						if(!(tabs.getSelectedComponent() instanceof JPanel)) pw.print(((JTextArea)((JScrollPane)(tabs.getSelectedComponent())).getViewport().getView()).getText());
//						else pw.print(((JTextArea)getJScrollPaneFromJPanel().getViewport().getView()).getText());
						fw.close();
						tabs.setTitleAt(tabs.getSelectedIndex(),file.getAbsolutePath().substring(file.getPath().lastIndexOf('\\')+1,file.getPath().length()));
					} catch (IOException ioe){
						System.out.println(ioe.getMessage());
					}
				}
			}else if (!file.isFile() && !file.isDirectory()){
				FileWriter fw = null;
				PrintWriter pw = null;
				try{
					fw = new FileWriter(file);
					pw = new PrintWriter(fw);
					if(!(tabs.getSelectedComponent() instanceof JPanel)) pw.print(((JTextArea)((JScrollPane)(tabs.getSelectedComponent())).getViewport().getView()).getText());
					else pw.print(((JTextArea)getJScrollPaneFromJPanel().getViewport().getView()).getText());
					fw.close();
					tabs.setTitleAt(tabs.getSelectedIndex(),file.getAbsolutePath().substring(file.getPath().lastIndexOf('\\')+1,file.getPath().length()));
				} catch (IOException ioe){
					System.out.println(ioe.getMessage());
				}
			}
		}
	}
	private void openFileContents(File file){
		JTextArea textArea = new JTextArea();
		JScrollPane scroll = new JScrollPane(textArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tabs.addTab(file.getName(), scroll);
		if(tabs.getTabCount()-1 > 0) tabs.setSelectedIndex(tabs.getSelectedIndex()+1);
		if(tabs.getTabCount()>0){
			edit_menu.setVisible(true);
			spellcheck_menu.setVisible(true);
		}
		undoManagers.add(addUndoToTab());
		try{
			Scanner sc = new Scanner(file);
			String contents = sc.useDelimiter("\\Z").next();
			textArea.setText(contents);
			sc.close();	
		}catch (FileNotFoundException fnfe){
			System.out.println(fnfe.getMessage());
		}catch (NoSuchElementException nsee){
			
		}
	}
	
	public void changeKBFile(String kb_file){
		keys = WordCorrectionCompletion.read_kb_to_hash(kb_file);
	}
	
	public UndoManager addUndoToTab(){
		UndoManager undoManager = new UndoManager();
		JTextArea curr_tab = getTextAreaFromTab();
		Document d = curr_tab.getDocument();
		d.addUndoableEditListener(new UndoableEditListener(){
			public void undoableEditHappened(UndoableEditEvent e){
				undoManager.addEdit(e.getEdit());
			}
		});
		addUndoActions(curr_tab, undoManager);
		return undoManager;
	}
	
	private void addUndoActions(JTextArea currArea, UndoManager undoManager){
		im = currArea.getInputMap(JComponent.WHEN_FOCUSED);
		am = currArea.getActionMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo");
		am.put(undo_item, new AbstractAction() {
		    public static final long serialVersionUID = 1;
		    public void actionPerformed(ActionEvent e) {
		        try {
		            if (undoManager.canUndo()) {
		                undoManager.undo();
		            }
		        } catch (CannotUndoException cue) {
		        	JOptionPane.showMessageDialog(null, "Cannot undo right now.");
		        }
		    }
		});
		am.put(redo_item, new AbstractAction() {
		    public static final long serialVersionUID = 1;
		    public void actionPerformed(ActionEvent e) {
		        try {
		            if (undoManager.canRedo()) {
		                undoManager.redo();
		            }
		        } catch (CannotRedoException cre) {
		            JOptionPane.showMessageDialog(null, "Cannot redo right now.");
		        }
		    }
		});
	}
	private JTextArea getTextAreaFromTab(){
		if(tabs.getSelectedComponent() instanceof JPanel){
			Component [] components = ((JPanel)tabs.getSelectedComponent()).getComponents();
			for(int i=0;i<components.length;i++){
				if(components[i] instanceof JScrollPane) return (JTextArea)((JScrollPane)components[i]).getViewport().getView();
			}
		}
		return (JTextArea)((JScrollPane)tabs.getSelectedComponent()).getViewport().getView();
	}
}
