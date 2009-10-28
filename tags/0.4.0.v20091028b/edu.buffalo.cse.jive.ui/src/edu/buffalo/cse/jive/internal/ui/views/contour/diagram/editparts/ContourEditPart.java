package edu.buffalo.cse.jive.internal.ui.views.contour.diagram.editparts;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LabelAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;

import edu.bsu.cs.jive.contour.Contour;
import edu.bsu.cs.jive.contour.ContourMember;
import edu.bsu.cs.jive.contour.ContourModel;
import edu.bsu.cs.jive.contour.Value;
import edu.bsu.cs.jive.contour.ContourMember.InnerClass;
import edu.bsu.cs.jive.contour.ContourMember.MethodDeclaration;
import edu.bsu.cs.jive.contour.ContourMember.Variable;
import edu.bsu.cs.jive.contour.java.InstanceContour;
import edu.bsu.cs.jive.contour.java.JavaContour;
import edu.bsu.cs.jive.contour.java.MethodContour;
import edu.bsu.cs.jive.contour.java.StaticContour;
import edu.bsu.cs.jive.util.ContourID;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.builders.InstanceContourFigureBuilder;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.builders.MethodContourFigureBuilder;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.builders.StaticContourFigureBuilder;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.builders.VariableFigureBuilder;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourFigure;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.ContourMemberTableFigure;
import edu.buffalo.cse.jive.internal.ui.views.contour.diagram.figures.JavaContourFigure;

/**
 * An {@code EditPart} serving as a controller for a {@code Contour}.  It is
 * responsible for creating the appropriate {@code ContourFigure} as well as
 * supplying the children and connections for the contour.
 * 
 * @see ContourDiagramEditPart
 * @see ContourReferenceEditPart
 * @see ContourFigure
 * @see JavaContourFigure
 * @author Jeffrey K Czyz
 */
public class ContourEditPart extends AbstractContourModelEditPart implements NodeEditPart {
	
	private ContourState contourState;
	
	private void setContourState() {
		ContourDiagramEditPart contents = (ContourDiagramEditPart) getViewer().getContents();
		switch (contents.getContourState()) {
		case MINIMIZED:
			contourState = minimizedState;
			break;
		case EXPANDED:
			contourState = expandedState;
			break;
		case STACKED:
			contourState = stackedState;
			break;
		default:
			contourState = stackedState;
		}
	}
	
	private boolean areMemberTablesShown() {
		ContourDiagramEditPart contents = (ContourDiagramEditPart) getViewer().getContents();
		return contents.areShowMemberTables();
	}
	
	private ContourState expandedState = new ContourState() {

		public IFigure createFigure() {
			JavaContour contour = (JavaContour) getModel();
			ContourModel model = contour.containingModel();
			ReentrantLock modelLock = model.getModelLock();
			modelLock.lock();
			try {
				ContourFigure.Importer importer =
					(ContourFigure.Importer) contour.accept(contourVisitor, null);
				final ContourFigure figure = new ContourFigure(ContourFigure.State.FULL, importer);
				
				if (areMemberTablesShown()) {
					contour.export(new Contour.Exporter() {
						public void addID(ContourID id) {
							// do nothing
						}
	
						public void addMember(ContourMember member) {
							ContourMemberTableFigure.MemberImporter importer =
								(ContourMemberTableFigure.MemberImporter) member.accept(contourMemberVisitor, null);
							if (importer != null) {
								figure.getMemberTable().addMember(importer);
							}
						}
	
						public void exportFinished() {
							// do nothing
						}
					});
				}
				
				return figure;
			}
			finally {
				modelLock.unlock();
			}
		}

		public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
			if (areMemberTablesShown()) {
				Contour contour = (Contour) getModel();
				final Value.ContourReference reference = (Value.ContourReference) connection.getModel();
				final List<ContourMember> memberList = new LinkedList<ContourMember>();
				
				Contour.Exporter exporter = new Contour.Exporter() {
					public void addID(ContourID id) {
						// do nothing
					}
	
					public void addMember(ContourMember member) {
						memberList.add(member);
					}
	
					public void exportFinished() {
						// do nothing
					}
				};
				
				ContourModel model = contour.containingModel();
				ReentrantLock modelLock = model.getModelLock();
				modelLock.lock();
				try {
					contour.export(exporter);
					for (int i = 0; i < memberList.size(); i++) {
						ContourMember member = memberList.get(i);
						Value value = member.value();
						if (value instanceof Value.ContourReference) {
							Value.ContourReference temp = (Value.ContourReference) value;
							if (temp == reference) {
								ContourFigure figure = (ContourFigure) getFigure();
								ContourMemberTableFigure memberTable = figure.getMemberTable();
								Label valueLabel = memberTable.getValueLabel(i);
								return new LabelAnchor(valueLabel);
							}
						}
					}
					
					return new ChopboxAnchor(getFigure());
				}
				finally {
					modelLock.unlock();
				}
			}
			else {
				return new ChopboxAnchor(getFigure());
			}
		}
		
		public void refreshVisuals() {
			if (areMemberTablesShown()) {
				Contour contour = (Contour) getModel();
				ContourModel model = contour.containingModel();
				ReentrantLock modelLock = model.getModelLock();
				modelLock.lock();
				try {
					final ContourFigure figure = (ContourFigure) getFigure();
					contour.export(new Contour.Exporter() {
						
						private int index = -1;
						
						public void addID(ContourID id) {
							// do nothing
						}
	
						public void addMember(ContourMember member) {
							index++;
							ContourMemberTableFigure.MemberImporter importer =
								(ContourMemberTableFigure.MemberImporter) member.accept(contourMemberVisitor, null);
							if (importer != null) {
								figure.getMemberTable().updateMember(index, importer);
							}
						}
	
						public void exportFinished() {
							// do nothing
						}
					});
				}
				finally {
					modelLock.unlock();
				}		
			}
		}
	};
	
	private ContourState stackedState = new ContourState() {

		public IFigure createFigure() {
			JavaContour contour = (JavaContour) getModel();
			final ContourModel model = contour.containingModel();
			ReentrantLock modelLock = model.getModelLock();
			modelLock.lock();
			try {
				return (IFigure) contour.accept(new JavaContour.Visitor() {
	
					public Object visit(InstanceContour contour, Object arg) {
						if (contour.isVirtual()) {
							for (Contour child : model.getChildren(contour)) {
								if (child instanceof MethodContour) {
									return new ContourFigure(ContourFigure.State.OUTLINE, null);
								}
							}
							
							return new ContourFigure(ContourFigure.State.EMPTY, null);
						}
						else {
							return createFullBuilder(contour);
						}
					}
	
					public Object visit(StaticContour contour, Object arg) {
						return createFullBuilder(contour);
					}
	
					public Object visit(MethodContour contour, Object arg) {
						return createFullBuilder(contour);
					}
					
					private Object createFullBuilder(JavaContour contour) {
						if (areMemberTablesShown()) {
							return expandedState.createFigure();
						}
						else {
							ContourFigure.Importer importer =
								(ContourFigure.Importer) contour.accept(contourVisitor, null);
							ContourFigure figure = new ContourFigure(ContourFigure.State.FULL, importer);
							return figure;
						}
					}
					
				}, null);
			}
			finally {
				modelLock.unlock();
			}
		}

		public ConnectionAnchor getSourceConnectionAnchor(final ConnectionEditPart connection) {
			if (areMemberTablesShown()) {
				JavaContour contour = (JavaContour) getModel();
				Object result = contour.accept(new JavaContour.Visitor() {

					public Object visit(InstanceContour contour, Object arg) {
						if (contour.isVirtual()) {
							return null;
						}
						else {
							return expandedState.getSourceConnectionAnchor(connection);
						}
					}

					public Object visit(StaticContour contour, Object arg) {
						// TODO Auto-generated method stub
						return null;
					}

					public Object visit(MethodContour contour, Object arg) {
						// TODO Auto-generated method stub
						if (connection instanceof ContourReferenceEditPart) {
							Value.ContourReference reference = (Value.ContourReference) connection.getModel();
							if (reference.toString().indexOf("#") != -1) {
								return new ChopboxAnchor(getFigure()) {
									public Point getLocation(Point reference) {
										Rectangle bounds = Rectangle.SINGLETON;
										bounds.setBounds(getBox());
										getOwner().translateToAbsolute(bounds);
										
										Point result = bounds.getTopLeft();
										return result;
									}
								};
							}
						}
						return null;
					}
					
				}, null);
				
				if (result == null) {
					return new ChopboxAnchor(getFigure());
				}
				else {
					return (ConnectionAnchor) result;
				}
			}
			else {
				if (connection instanceof ContourReferenceEditPart) {
					Value.ContourReference reference = (Value.ContourReference) connection.getModel();
					if (reference.toString().indexOf("#") != -1) {
						return new ChopboxAnchor(getFigure()) {
							public Point getLocation(Point reference) {
								Rectangle bounds = Rectangle.SINGLETON;
								bounds.setBounds(getBox());
								getOwner().translateToAbsolute(bounds);
								
								Point result = bounds.getTopLeft();
								return result;
							}
						};
					}
				}
				
				return new ChopboxAnchor(getFigure());
			}
		}
		
		public void refreshVisuals() {
			if (areMemberTablesShown()) {
				JavaContour contour = (JavaContour) getModel();
				contour.accept(new JavaContour.Visitor() {

					public Object visit(InstanceContour contour, Object arg) {
						if (!contour.isVirtual()) {
							expandedState.refreshVisuals();
						}

						return null;
					}

					public Object visit(StaticContour contour, Object arg) {
						expandedState.refreshVisuals();
						return null;
					}

					public Object visit(MethodContour contour, Object arg) {
						expandedState.refreshVisuals();
						return null;
					}
					
				}, null);
			}
			// do nothing
		}
	};
	
	private ContourState minimizedState = new ContourState() {

		public IFigure createFigure() {
			JavaContour contour = (JavaContour) getModel();
			Object result = contour.accept(new JavaContour.Visitor() {

				public Object visit(InstanceContour contour, Object arg) {
					if (contour.isVirtual()) {
						return ContourFigure.State.EMPTY;
					}
					else {
						return ContourFigure.State.NODE;
					}
				}

				public Object visit(StaticContour contour, Object arg) {
					// TODO Auto-generated method stub
					return ContourFigure.State.FULL;
				}

				public Object visit(MethodContour contour, Object arg) {
					// TODO Auto-generated method stub
					return ContourFigure.State.EMPTY;
				}
				
			}, null);
			
			ContourFigure.State figureState = (ContourFigure.State) result;
			if (figureState == ContourFigure.State.EMPTY) {
				return new ContourFigure(figureState, null);	
			}
			else {
				ContourModel model = contour.containingModel();
				ReentrantLock modelLock = model.getModelLock();
				modelLock.lock();
				try {
					ContourFigure.Importer importer =
						(ContourFigure.Importer) contour.accept(contourVisitor, null);
					ContourFigure figure = new ContourFigure(figureState, importer);
					return figure;
				}
				finally {
					modelLock.unlock();
				}
			}
		}

		public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
			return new ChopboxAnchor(getFigure());
		}
		
		public void refreshVisuals() {
			// do nothing
		}
	};
	
	public ContourEditPart() {
		contourState = stackedState;
	}
	
	// TODO Determine why this doesn't work.  Overriding add() and remove()
	// in ContourFigure works instead 
	// TODO Note that this might not be wanted anyway, if we make contour
	// members children
//	public IFigure getContentPane() {
//		ContourFigure figure = (ContourFigure) getFigure();
//		return figure.getChildCompartment();
//	}
	
	/**
	 * A visitor used to create the appropriate figure.
	 */
	private JavaContour.Visitor contourVisitor = new JavaContour.Visitor() {

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.contour.java.JavaContour.Visitor#visit(edu.bsu.cs.jive.contour.java.InstanceContour, java.lang.Object)
		 */
		public Object visit(InstanceContour contour, Object arg) {
			return new InstanceContourFigureBuilder(contour.id());
		}

		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.contour.java.JavaContour.Visitor#visit(edu.bsu.cs.jive.contour.java.StaticContour, java.lang.Object)
		 */
		public Object visit(StaticContour contour, Object arg) {
			return new StaticContourFigureBuilder(contour.id());
		}
		
		/* (non-Javadoc)
		 * @see edu.bsu.cs.jive.contour.java.JavaContour.Visitor#visit(edu.bsu.cs.jive.contour.java.MethodContour, java.lang.Object)
		 */
		public Object visit(MethodContour contour, Object arg) {
			return new MethodContourFigureBuilder(contour.id());
		}
	};
	
	private ContourMember.Visitor contourMemberVisitor = new ContourMember.Visitor() {

		public Object visit(MethodDeclaration m, Object arg) {
			return null;
		}

		public Object visit(Variable v, Object arg) {
			return new VariableFigureBuilder(v);
		}

		public Object visit(InnerClass c, Object arg) {
			return null;
		}
		
	};

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		setContourState();
		return contourState.createFigure();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new NonResizableEditPolicy());
		// TODO Determine what other edit policies should be added, if any
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List getModelChildren() {
		Contour contour = (Contour) getModel();
		ContourModel model = contour.containingModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			// TODO Add contour members as children?
			return model.getChildren(contour);
		}
		finally {
			modelLock.unlock();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	 */
	protected List getModelSourceConnections() {
		Contour contour = (Contour) getModel();
		final List<Value.ContourReference> memberList = new LinkedList<Value.ContourReference>();
		Contour.Exporter exporter = new Contour.Exporter() {
			public void addID(ContourID id) {
				// do nothing
			}

			public void addMember(ContourMember member) {
				if (member instanceof ContourMember.Variable) {
					ContourMember.Variable variable = (ContourMember.Variable) member;
					Value value = variable.value();
					if (value instanceof Value.ContourReference) {
						Value.ContourReference reference = (Value.ContourReference) value;
						memberList.add(reference);
					}
				}
			}

			public void exportFinished() {
				// do nothing
			}
		};
		
		contour.export(exporter);
		return memberList;
	}
	
	// TODO Determine if there is a more efficient way of accomplishing this
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	 */
	protected List getModelTargetConnections() {
		Contour contour = (Contour) getModel();
		final ContourID id = contour.id();
		final List<Value.ContourReference> memberList = new LinkedList<Value.ContourReference>();
		
		final Contour.Exporter exporter = new Contour.Exporter() {	
			public void addID(ContourID id) {
				// do nothing
			}

			public void addMember(ContourMember member) {
				if (member instanceof ContourMember.Variable) {
					ContourMember.Variable variable = (ContourMember.Variable) member;
					Value value = variable.value();
					if (value instanceof Value.ContourReference) {
						Value.ContourReference reference = (Value.ContourReference) value;
						if (reference.toString().equals(id.toString())) {
							memberList.add(reference);
						}
					}
				}
			}

			public void exportFinished() {
				// do nothing
			}
		};
		
		ContourModel model = contour.containingModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			ContourModel.Visitor contourModelVisitor = new ContourModel.Visitor() {
				public void visit(Contour contour) {
					contour.export(exporter);
				}
			};
			
			model.visitDepthFirst(contourModelVisitor);
			return memberList;
		}
		finally {
			modelLock.unlock();
		}
	}
	
	protected void refreshVisuals() {
		setContourState();
		contourState.refreshVisuals();
	}
	
	/**
	 * Adds a child {@code EditPart} for the supplied {@code Contour} if one
	 * does not already exist. 
	 * 
	 * @param contour the child contour
	 */
	void addChildContour(Contour contour) {
		// Create a new child edit part if one doe not already exist.  Instance
		// contours for an object are created together, so during normal
		// execution this case will not be reached.  However, while replaying
		// recorded states it will.
		if (getContourEditPart(contour) == null) { 
			EditPart childPart = createChild(contour);
			addChild(childPart, -1);
			
			// Method contours may already have connections during back stepping
			if (contour instanceof MethodContour) {
				updateChildConnections(contour);
			}
		}
	}
	
	/**
	 * Removes the {@code EditPart} associated with the supplied {@code Contour}
	 * if one exists.  For {@code MethodContour}s, connections are first
	 * updated to reflect the fact that the contour has been removed from the
	 * model.
	 * 
	 * @param contour the child contour
	 */
	void removeChildContour(Contour contour) {
		// Remove any connections originating from the contour
		if (contour instanceof MethodContour) {
			updateChildConnections(contour);
		}
		
		// Remove the edit part if it exists
		EditPart childPart = getContourEditPart(contour);
		if (childPart != null) {
			removeChild(childPart);
		}
	}
	
	/**
	 * Updates the {@code ContourEditPart}s that are connected with the supplied
	 * {@code Contour} via its {@code ContourMember}s. 
	 * 
	 * @param contour the contour with connections to update 
	 */
	private void updateChildConnections(final Contour contour) {
		final ContourModel model = contour.containingModel();
		ReentrantLock modelLock = model.getModelLock();
		modelLock.lock();
		try {
			Contour.Exporter exporter = new Contour.Exporter() {
				public void addID(ContourID id) {
					// do nothing
				}
	
				public void addMember(ContourMember member) {
					// TODO Determine what other members imply connections
					if (member instanceof ContourMember.Variable) {
						ContourMember.Variable variable = (ContourMember.Variable) member;
						Value v = variable.value();
						if (v instanceof Value.ContourReference) {
							Value.ContourReference ref = (Value.ContourReference) v;
							ContourID id = ref.getContourID();
							if (model.contains(id)) {
								ContourEditPart part = getContourEditPart(model.getContour(id));
								if (part != null) {
									part.refreshTargetConnections();
								}
								else {
									System.out.println("ContourEditPart#updateChildConnections : contour = " + contour.id());
								}
							}
							// TODO Otherwise, do we need to search for the edit part?
							else {
								System.out.println("ContourEditPart#updateChildConnections : v = " + id);
							}
						}
					}
				}
	
				public void exportFinished() {
					// do nothing
				}
			};
			
			contour.export(exporter);
		}
		finally {
			modelLock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		setContourState();
		return contourState.getSourceConnectionAnchor(connection);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		// TODO Determine if this should be implemented
		System.out.println("ContourEditPart#getSourceConnectionAnchor(Request) - " + request );
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		// TODO Determine if this should be implemented
		System.out.println("ContourEditPart#getTargetConnectionAnchor(Request) - " + request );
		return null;
	}
	
	private interface ContourState {
		public IFigure createFigure();
		public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection);
		public void refreshVisuals();
	}
}
