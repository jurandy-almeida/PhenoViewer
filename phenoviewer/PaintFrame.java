package phenoviewer;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.GroupLayout.Alignment;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class PaintFrame extends JFrame
{

	private static int border = 50;
	private PaintBrush paint;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	public PaintFrame(BufferedImage original, BufferedImage maskor, boolean edit) {
		setTitle("Create Mask");
//        setSize(1280,960+border);
//        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.width = Math.min(1280+50, screenSize.width);
        screenSize.height = Math.min(960+100,screenSize.height-50);
        setPreferredSize(screenSize);
        setResizable(false);

        Action action = new AbstractAction("saveMask") {
   		public void actionPerformed(ActionEvent e) {
       		 paint.save();
       	 } };

        Action action2 = new AbstractAction("reset") {
   		public void actionPerformed(ActionEvent e) {
   			paint.reset();
       	 } };

        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
        JButton saveButton = new JButton(action);
        saveButton.setHorizontalAlignment(SwingConstants.LEFT);
        buttonGroup.add(saveButton);
        saveButton.setToolTipText("Hotkey: S");
        saveButton.setText("Save Mask");
        saveButton.getActionMap().put("saveMask", action);
        saveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
       		 (KeyStroke) action.getValue(Action.ACCELERATOR_KEY), "saveMask");

        action2.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, 0));
        JButton resetButton = new JButton(action2);
        buttonGroup.add(resetButton);
        resetButton.setToolTipText("Hotkey: R");
        resetButton.setText("Reset Mask");
        resetButton.getActionMap().put("reset", action2);
        resetButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
       		 (KeyStroke) action2.getValue(Action.ACCELERATOR_KEY), "reset");


        Action close = new AbstractAction("close") {
   		public void actionPerformed(ActionEvent e) {
   			paint.close();
       	 } };

   	 close.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ESCAPE"));


        Action incBrush = new AbstractAction("incBrush") {
   		public void actionPerformed(ActionEvent e) {
   			paint.setBrush(2);
       	 } };

        Action decBrush = new AbstractAction("decBrush") {
   		public void actionPerformed(ActionEvent e) {
   			paint.setBrush(-2);
       	 } };

         Action setErase = new AbstractAction("decBrush") {
    		public void actionPerformed(ActionEvent e) {
    			paint.setErase();
    	 } };

         Action createPolygon = new AbstractAction("createPolygon") {
    		public void actionPerformed(ActionEvent e) {
    			paint.createPolygon();
    	 } };

         Action moveLeft = new AbstractAction("moveLeft") {
    		public void actionPerformed(ActionEvent e) {
    			paint.moveLeft();
    	 } };
         Action moveRight = new AbstractAction("moveRight") {
    		public void actionPerformed(ActionEvent e) {
    			paint.moveRight();
    	 } };
         Action moveDown = new AbstractAction("moveDown") {
    		public void actionPerformed(ActionEvent e) {
    			paint.moveDown();
    	 } };
         Action moveUp = new AbstractAction("moveUp") {
    		public void actionPerformed(ActionEvent e) {
    			paint.moveUp();
    	 } };
         Action zoomIn = new AbstractAction("zoomIn") {
    		public void actionPerformed(ActionEvent e) {
    			paint.zoomIn();
    	 } };
         Action zoomOut = new AbstractAction("zoomOut") {
    		public void actionPerformed(ActionEvent e) {
    			paint.zoomOut();
    	 } };
         Action helpAction = new AbstractAction("helpAction") {
    		public void actionPerformed(ActionEvent e) {
    			helpAction();
    	 }

    		private void helpAction() {
          //To-Do
    		} };


   	    incBrush.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, 0));
        JButton incButton = new JButton(incBrush);
        buttonGroup.add(incButton);
        incButton.setToolTipText("Hotkey: [");
        incButton.setText("Increase Brush");
        incButton.getActionMap().put("incBrush", incBrush);
        incButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
       		 (KeyStroke) incBrush.getValue(Action.ACCELERATOR_KEY), "incBrush");

        decBrush.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, 0));
        JButton decButton = new JButton(decBrush);
        buttonGroup.add(decButton);
        decButton.setToolTipText("Hotkey: ]");
        decButton.setText("Decrease Brush");
        decButton.getActionMap().put("decBrush", decBrush);
        decButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
       		 (KeyStroke) decBrush.getValue(Action.ACCELERATOR_KEY), "decBrush");

        setErase.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, 0));
        JButton eraseButton = new JButton(setErase);
        buttonGroup.add(eraseButton);
        eraseButton.setToolTipText("Hotkey: E");
        eraseButton.setText("Erase / Paint");
        eraseButton.getActionMap().put("setErase", setErase);
        eraseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
       		 (KeyStroke) setErase.getValue(Action.ACCELERATOR_KEY), "setErase");

        //polygon
        createPolygon.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
        JButton polygonButton = new JButton(createPolygon);
        buttonGroup.add(polygonButton);
        polygonButton.setToolTipText("Hotkey: P");
        polygonButton.setText("Create Polygon");
        polygonButton.getActionMap().put("createPolygon", createPolygon);
        polygonButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
       		 (KeyStroke) createPolygon.getValue(Action.ACCELERATOR_KEY), "createPolygon");

        //help
        createPolygon.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, 0));
        JButton helpButton = new JButton(helpAction);
        buttonGroup.add(helpButton);
        helpButton.setToolTipText("Hotkey: P");
        helpButton.setBackground(Color.red);
        helpButton.setText("Help");
        helpButton.getActionMap().put("helpAction", helpAction);
        helpButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
       		 (KeyStroke) helpAction.getValue(Action.ACCELERATOR_KEY), "helpAction");

        //usando polygonButton para mapear mais a�oes: mover mascara
        moveLeft.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
        polygonButton.getActionMap().put("moveLeft", moveLeft);
        polygonButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
       		 (KeyStroke) moveLeft.getValue(Action.ACCELERATOR_KEY), "moveLeft");
        moveRight.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
        polygonButton.getActionMap().put("moveRight", moveRight);
        polygonButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
       		 (KeyStroke) moveRight.getValue(Action.ACCELERATOR_KEY), "moveRight");
        moveDown.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        polygonButton.getActionMap().put("moveDown", moveDown);
        polygonButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
       		 (KeyStroke) moveDown.getValue(Action.ACCELERATOR_KEY), "moveDown");
        moveUp.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
        polygonButton.getActionMap().put("moveUp", moveUp);
        polygonButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
       		 (KeyStroke) moveUp.getValue(Action.ACCELERATOR_KEY), "moveUp");

        // zoom
        zoomIn.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0));
        polygonButton.getActionMap().put("zoomIn", zoomIn);
        polygonButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
       		 (KeyStroke) zoomIn.getValue(Action.ACCELERATOR_KEY), "zoomIn");
        zoomOut.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0));
        polygonButton.getActionMap().put("zoomOut", zoomOut);
        polygonButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
       		 (KeyStroke) zoomOut.getValue(Action.ACCELERATOR_KEY), "zoomOut");

        paint = new PaintBrush(original, maskor, edit);
        paint.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scrollPane = new JScrollPane(paint);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setViewportBorder(new LineBorder(new Color(0, 0, 0)));
        scrollPane.setPreferredSize(new Dimension(Math.min(screenSize.width,1280+20),screenSize.height-100));

        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        				.addGroup(groupLayout.createSequentialGroup()
        					.addComponent(saveButton)
        					.addGap(5)
        					.addComponent(resetButton)
        					.addGap(5)
        					.addComponent(incButton)
        					.addGap(5)
        					.addComponent(decButton)
        					.addGap(5)
        					.addComponent(eraseButton)
        					.addGap(5)
        					.addComponent(polygonButton)
        					.addGap(5)
        					.addComponent(helpButton))
        				.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addContainerGap(604, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
        	groupLayout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        				.addComponent(saveButton)
        				.addComponent(resetButton)
        				.addComponent(incButton)
        				.addComponent(decButton)
        				.addComponent(eraseButton)
        				.addComponent(polygonButton)
        				.addComponent(helpButton))
        			.addGap(13)
        			.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addGap(603))
        );
        getContentPane().setLayout(groupLayout);
        pack();
        this.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		paint.close();
        		System.gc();
        	}
        });

        setVisible(true);
	}


}
