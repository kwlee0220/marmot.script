
def jdbcOptions = jdbcConnection {
				url "jdbc:postgresql://129.254.82.95:5433/sbdata"
				user "sbdata"
				passwd "urc2004"
				driverClass "org.postgresql.Driver" }

load("POI/노인복지시설")
load("POI/노인복지시설", splitCount: 10)

loadTextFile('교통/지하철/서울역사')
loadTextFile("교통/지하철/서울역사", "xxx", splitCount: 10)

filter("induty_nm == '경로당'") 
filter("induty_nm == '경로당'", initializer: "__XXX__")

project("the_geom")

defineColumn('age:double')
defineColumn('age:double', "xxxxx")
defineColumn('age:double', "xxxxx", initializer: 'YYYY')

update("ENG_SUB_NM = ENG_SUB_NM.toUpperCase()")
update('$money_formatter.parse(거래금액).intValue()',
		initializer: '$money_formatter = new DecimalFormat("#,###,###")')

expand("age:double,gpa:double")
expand("age:double,gpa:double", "xxxx")
expand("age:double,gpa:double", "xxxx", initializer: "yyy")

take(10)
pickTopK('age:A', 10)
sort('age:A')
distinct('age,name')
distinct('age,name', workerCount: 10)

assignUid('id')
sample(0.1)
shard(10)

aggregate({count=count(); total=sum('age'); avg=avg('age')})
aggregateByGroup('kcol', tags: 'the_geom', workerCount: 17) {
	count=count(); total=sum('age'); avg=avg('age')
}
				
takeByGroup('kcol', 10, orderBy: 'ocol3:A,ocol4:B')
listByGroup('col1,col2', tags: 'col3,col4', orderBy: 'col5:DESC,col6:ASC') 

storeByGroup('col1', "/tmp/result")
storeByGroup('col1', "/tmp/result", force:true)

def outSchema = schema("name:string,age:int,value:double")
reduceToSingleRecordByGroup('col1', outSchema, 'tag', 'value')

parseCsv('text') {
	delim '|'
	quote '"'
	escape "\\"
	header "the_geom:point,sig_cd,emd_cd,pnu,age:int,transit_yn:boolean"
	trimColumn false
	nullValue '0'
	throwParseError false
}

hashJoin('col1,col2', 'POI/노인복지시설', 'col1,jcol2', output: 'left.*')
hashJoin('col1,col2', 'POI/노인복지시설', 'col1,jcol2', output: 'left.*', workerCount: 5)
hashJoin('col1,col2', 'POI/노인복지시설', 'col1,jcol2', output: '*,param.{C1,C2}',
			type: 'LEFT_OUTER_JOIN', workerCount: 5)

def rightCols = (2011..2017).collect { "gas_$it" }.join(',')
loadHashJoinFile('구역/연속지적도', 'PNU', 'tmp/anyang/gas_side_by_side', 'PNU',
				output: "left.*,right.{$rightCols}", type: 'LEFT_OUTER_JOIN', workerCount: 17)

buffer('the_geom', distance('1km'))
buffer('the_geom', 1000, output: 'the_geom2', segmentCount: 16, throwOpError:true)

centroid('the_geom')
centroid('the_geom', output: 'the_geom2')

transformCrs('the_geom', 'EPSG:5186', 'EPSG:4326')

intersection('the_geom', wkt('POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))'))

toXY 'the_geom', 'x_pos', 'y_pos'
toXY 'the_geom', 'x_pos', 'y_pos', keepGeomColumn: true
toPoint 'x_pos', 'y_pos', 'the_geom' 

filterSpatially('the_geom', intersects, wkt('POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))')) 
filterSpatially('the_geom', withinDistance('4km'), envelope(minx: 0, miny: 0, maxx: 1, maxy:1))
filterSpatially('the_geom', intersects, dataset('구역/시도').bounds) 

query 'POI/노인복지시설', envelope(minx: 0, miny: 0, maxx: 1, maxy:1) 
query 'POI/노인복지시설', wkt('POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))')
query 'POI/노인복지시설', dataset('xxx'), negated:true

spatialJoin 'the_geom', "tmp/10min/high_density_center", output: 'the_geom,*-{the_geom}'
spatialJoin 'the_geom', "tmp/10min/high_density_center", output: "*,param.col1"
spatialJoin 'the_geom', "tmp/10min/high_density_center", output: "*,param.col1",
			joinExpr: withinDistance('1km')

spatialSemiJoin 'the_geom', "tmp/10min/high_density_center"
spatialSemiJoin 'the_geom', "tmp/10min/high_density_center",
				joinExpr: withinDistance('100m'), negated: true
spatialOuterJoin 'the_geom', "tmp/10min/high_density_center", output: "*,param.col1"

clipJoin 'the_geom', "tmp/10min/high_density_hdong"
intersectionJoin 'the_geom', "tmp/10min/high_density_hdong", output: "*"

loadGrid squareGrid(envelope(minx: 10, miny: 20, maxx: 30, maxy:40), size2d('10x20'))
loadGrid squareGrid(dataset('xxx'), size2d('10x20')), splitCount: 7

assignGridCell('the_geom', squareGrid(envelope(minx: 10, miny: 20, maxx: 30, maxy:40), size2d('10x20')))
assignGridCell('the_geom', squareGrid(dataset('xxx'), size2d('10x20')), assignOutside: true)

storeAsCsv 'tmp/csv', delim: '|', quote: '"', charset: 'euc-kr'
storeAsCsv 'tmp/csv'

loadJdbcTable('subway_stations', jdbcOptions,
				selectExpr: "ST_AsBinary(the_geom) as the_geom",
				mapperCount: 17)

storeIntoJdbcTable('test', jdbcOptions, valueExpr: "into_value_expr")
