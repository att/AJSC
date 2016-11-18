package ajsc

class ServiceProperty  implements Serializable{
	private static final long serialVersionUID = 1L;

	String name
	String value
	boolean deletedFlag=false
	static transients = [ 'deletedFlag' ]

//	static belongsTo = [ parent : ServiceProperties ]
    static constraints = {
    	value(maxSize:1000)
//    	parent(nullable:true)
    }
    static mapping = {
		id generator:'assigned'
	}
	static mapWith = "none"
	
	String toString() {
		return "name: ${this.name} value: ${this.value}"
	}
}
