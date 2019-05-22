package marmot.script.plan


def methodMissing(String name, args) {
	if ( name != 'plan') {
		println "!!!!!!! methodMissing(GPlanScriptParser): name=$name, args=$args"
		return;
	}
	
	OperatorFactoryAssembler assembler = new OperatorFactoryAssembler(args[0]);
	Closure script = (Closure)args[1]
	script.delegate = assembler
	script.resolveStrategy = Closure.DELEGATE_ONLY;
	script()
	assembler.assemble();
}

def test() {
println plan ("인구밀도_10000이상_행정동추출") {
	load "POI/노인복지시설"
	load "POI/노인복지시설" splits 10
	filter "induty_nm == '경로당'"
	filter "induty_nm == '경로당'" initializer "__XXX__"
	project "the_geom"
	
	define_column 'age:double'
	define_column 'age:double' set "xxxxx"
	define_column 'age:double' set "xxxxx" initializer 'YYYY'
	
	update "ENG_SUB_NM = ENG_SUB_NM.toUpperCase()"
	update "$money_formatter.parse(거래금액).intValue()" options {
		initializer '$money_formatter = new DecimalFormat("#,###,###")'
	}
	
	buffer 'the_geom' by '1km'
	buffer 'the_geom' by '1km' to 'the_geom2'
	buffer 'the_geom' by '100m' to 'the_geom2' segments 16
	
	spatial semi_join on 'the_geom' with "tmp/10min/high_density_center"
	spatial semi_join with "tmp/10min/high_density_center"
	spatial semi_join with "tmp/10min/high_density_center" within_distance '1km'
	spatial semi_join with "tmp/10min/high_density_center" negated
	
	spatial join with "tmp/10min/high_density_center"
}
}

test()
