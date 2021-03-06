package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JScrollPane;


public class Chat_Client implements Runnable {

	private Socket socket;
	private PrintWriter out = null;
	private Thread t3, t4;
	private JTextField txtMessage;
	private JPanel contentPane;
	private static volatile JFrame chat = null;
	private static volatile JTextPane mes = null;
	private static volatile JLabel txtLogin = null;
	private static volatile String login;
	private JTextPane ConnectedPane;
	private static volatile JSplitPane splitPane2;
	
	public Chat_Client(Socket s, String l){
		socket = s;
		login = l;
		
		chat = new JFrame();
		chat.setIconImage(Toolkit.getDefaultToolkit().getImage("/home/maxouille/Eclipse/Java/PCV/src/Chat-gris-icon.png"));
		chat.setTitle("Mini-Chat");
		chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chat.setLocationRelativeTo(null);
		chat.setSize(450,300);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		chat.setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		/** MENU BAR **/
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBorder(new LineBorder(Color.BLACK));
		contentPane.add(menuBar, BorderLayout.NORTH);
				
		JMenuItem mntmDisconnect = new JMenuItem("Disconnect");
		mntmDisconnect.setAction(DisConnectAction);
		menuBar.add(mntmDisconnect);
		
		JMenuItem mntmClear = new JMenuItem("Clear");
		mntmClear.setAction(ClearAction);
		menuBar.add(mntmClear);
				
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setAction(quitAction);
		menuBar.add(mntmExit);
		
		/** MESSAGE BAR **/
		
		
		/** PARTIE GAUCHE **/
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(310);
		
		// PARTIE GAUCHE SPLITPANE
		splitPane2 = new JSplitPane();
		
		//PARTIE GAUCHE SPLITPANE2
		txtLogin = new JLabel(" "+login+" : ");
		splitPane2.setLeftComponent(txtLogin);
		
		//PARTIE DROITE SPLITPANE2
		txtMessage = new JTextField();
		txtMessage.setToolTipText("Message");
		splitPane2.setRightComponent(txtMessage);
		txtMessage.setColumns(10);
		txtMessage.addActionListener(new SendAction());
		
		splitPane.setLeftComponent(splitPane2);
		
		//PARTIE DROITE SPLITPANE
		JButton btnEnvoyer = new JButton("Envoyer");
		splitPane.setRightComponent(btnEnvoyer);
		btnEnvoyer.addActionListener(new SendAction());
		
		contentPane.add(splitPane, BorderLayout.SOUTH);
		
		
		/*JPanel Connected = new JPanel();
		Connected.setBorder(new LineBorder(Color.BLACK));
		Connected.setPreferredSize(new Dimension(100,100));
		contentPane.add(Connected, BorderLayout.WEST);
		Connected.setLayout(new BorderLayout(0, 0));
		
		JLabel lblConnected = new JLabel("Connected");
		lblConnected.setAlignmentX(Component.CENTER_ALIGNMENT);
		Connected.add(lblConnected, BorderLayout.NORTH);
		
		ConnectedPane = new JTextPane();
		Connected.add(ConnectedPane);
		*/
		/** PARTIE DROITE **/
		/*JPanel Admin = new JPanel();
		Admin.setBorder(new LineBorder(Color.BLACK));
		Admin.setPreferredSize(new Dimension(100,100));
		contentPane.add(Admin, BorderLayout.EAST);
		
		JLabel lblAdmin = new JLabel("Admin");
		Admin.add(lblAdmin);
		*/
		/** PARTIE CENTRALE **/
		JPanel Chat = new JPanel();
		Chat.setLayout(new BoxLayout(Chat, BoxLayout.PAGE_AXIS));
		Chat.setBorder(new LineBorder(Color.BLACK));
		
		JScrollPane scrollPane = new JScrollPane(Chat);	
		
		mes = new JTextPane();
		mes.setEditable(false);
		
		SimpleAttributeSet style_normal = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style_normal, "Calibri");
		StyleConstants.setFontSize(style_normal, 10);

		/*
		 * Création du style pour l'affichage du titre
		 */
		SimpleAttributeSet style_titre = new SimpleAttributeSet();
		style_titre.addAttributes(style_normal);
		StyleConstants.setForeground(style_titre, Color.RED);
		StyleConstants.setFontSize(style_titre, 14);
		
		/*
		 * Création du style qui permet de centrer le texte
		 */
		SimpleAttributeSet centrer = new SimpleAttributeSet();
		StyleConstants.setAlignment(centrer, StyleConstants.ALIGN_LEFT);
		
		try {
			//Récupération du style du document 
			StyledDocument doc = mes.getStyledDocument();
			
			/*
			 * Insertion d'une chaine de caractères dans le document
			 * insertString :
			 * 	position de départ dans le document (doc.getLength ajoute à la fin
			 *  texte à ajouter
			 *  style pour le texte à ajouter
			 */
			doc.insertString(doc.getLength(), "Bienvenue sur le Chat !"+"\n\n", style_titre);
			int fin_titre=doc.getLength();
			
			/*
			 * Centrage du titre
			 */
			doc.setParagraphAttributes(0, fin_titre, centrer, false);
			
		}
		catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		Chat.add(mes);
		
		scrollPane.getViewport().setViewSize(mes.getPreferredSize());
		JScrollBar vert_bar = scrollPane.getVerticalScrollBar();
		vert_bar.setValue(vert_bar.getMaximum());
		contentPane.add(scrollPane);
		chat.setVisible(true);
	}
	
	/**
	 * Action déclenchée lorsque l'on clique sur le bouton quit ou sur l'item
	 * de menu quit.
	 */
	private final Action quitAction = new QuitAction();

	/**
	 * Action déclenchée lorsque l'on clique sur le bouton disconnect ou sur l'item
	 * de menu disconnet.
	 */
	private final Action DisConnectAction = new DisConnectAction();
	
	/**
	 * Action déclenchée lorsque l'on clique sur le bouton quit ou sur l'item
	 * de menu quit.
	 */
	private final Action ClearAction = new ClearAction();
	
	public void run() {
		try {
			// On créé un flux d'entré pour recevoir et un flux de sortie pour écrire.	
			out = new PrintWriter(socket.getOutputStream());
			
			t3 = new Thread(new ReceptionMessages(socket, mes, txtLogin, login, chat, splitPane2));
			t3.start();
			
			//t4 = new Thread(new ConnectedRefresh(socket, ConnectedPane));
			//t4.start();
		} 
		catch (IOException e) {
			System.err.println("Le serveur distant s'est déconnecté !");
			try {
				socket.close();
			} catch (IOException e1) {
				System.err.println("Erreur fermeture socket dans chat_clientserveur");
			}
		}
	}
	
	
	/**
	 * Action pour quitter l'application
	 */
	private class QuitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * Constructeur de l'action pour quitter l'application.
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public QuitAction()
		{
			putValue(NAME, "Quit");
			/*
			 * Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
			 * 	= InputEvent.CTRL_MASK on win/linux
			 *  = InputEvent.META_MASK on mac os
			 */
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/Quit.png"));
			putValue(SMALL_ICON,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/Quit_small.png"));
			putValue(SHORT_DESCRIPTION, "Quits the application");
		}

		/**
		 * Opérations réalisées par l'action
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 *            ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			/*
			 * Action à effectuer lorsque l'action "quit" est cliquée :
			 * sortir avec un System.exit() (pas très propre, mais fonctionne)
			 */
			try {
				//On ferme la socket
				socket.close();
			} 
			catch (IOException e1) {
				System.err.println("Erreur fermeture socket");
			}
			//On quitte
			System.exit(0);
		}
	}
	

	/**
	 * Action pour se connecter au serveur
	 */
	private class DisConnectAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * Constructeur de l'action pour se déconnecter du serveur
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public DisConnectAction() {
			putValue(NAME, "Disconnect");
			/*
			 * Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
			 * 	= InputEvent.CTRL_MASK on win/linux
			 *  = InputEvent.META_MASK on mac os
			 */
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/Delete.png"));
			putValue(SMALL_ICON,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/Delete_small.png"));
			putValue(SHORT_DESCRIPTION, "Disconnects to the server");
		}

		/**
		 * Opérations réalisées par l'action
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 *            ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			/*
			 * Action à effectuer lorsque l'action "quit" est cliquée :
			 * sortir avec un System.exit() (pas très propre, mais fonctionne)
			 */
			try {
				//On cache le chat
				chat.setVisible(false);
				chat.dispose();
				//On ferme la socket
				socket.close();
				//On relance la fenetre de connexion
				new Client();	
			} 
			catch (IOException e1) {
				System.err.println("Erreur fermeture socket disconnect");
			}
		}
	}
	

	/**
	 * Action pour envoyer un message
	 */
	private class SendAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * Constructeur de l'action pour se déconnecter du serveur
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public SendAction() {
			putValue(NAME, "Send");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/Black.png"));
			putValue(SMALL_ICON,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/Black_small.png"));
			putValue(SHORT_DESCRIPTION, "Send message to the server");
		}

		/**
		 * Opérations réalisées par l'action
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 *            ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			
			//On récupère ce qu'on vient d'écrire
			String message = txtMessage.getText();
			if (!message.equals("")) {
				//On envoie le message au serveur
				out.println(message);
				out.flush();
				txtMessage.setText("");
			}
		}
	}
	
	/**
	 * Action pour effacer les messages
	 */
	private class ClearAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * Constructeur de l'action pour quitter l'application.
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public ClearAction()
		{
			putValue(NAME, "Clear");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/Deconnexion.png"));
			putValue(SMALL_ICON,
					new ImageIcon("/home/maxouille/Eclipse/Java/PCV/src/images/Deconnexion.png"));
			putValue(SHORT_DESCRIPTION, "Clear the dashboard");
		}

		/**
		 * Opérations réalisées par l'action
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 *            ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			mes.setText("");
		}
	}
}